package com.besant.ecommerce.main;

import com.besant.ecommerce.discount.Discount;
import com.besant.ecommerce.exception.ShopException;
import com.besant.ecommerce.model.*;
import com.besant.ecommerce.service.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ApiServer {
    private final ProductService products = new ProductService();
    private final CustomerService customers = new CustomerService();
    private final DiscountService discounts = new DiscountService();
    private final PaymentService payments = new PaymentService();
    private final CartService carts = new CartService(products, discounts);
    private final OrderService orders = new OrderService(products, payments);
    private final Path webDir;

    public ApiServer(Path webDir) {
        this.webDir = webDir.toAbsolutePath().normalize();
    }

    public void start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/", this::handleApi);
        server.createContext("/", this::handleStatic);
        server.start();
        System.out.println("E-Commerce server running on http://localhost:" + port);
        System.out.println("Press Ctrl+C to stop.");
    }

    // ---------------------------------------------------------------- web files
    private void handleStatic(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        if (path.equals("/")) path = "/index.html";
        Path file = webDir.resolve(path.substring(1)).normalize();
        if (!file.startsWith(webDir) || !Files.isRegularFile(file)) {
            send(ex, 404, "text/plain; charset=utf-8", "Not found".getBytes(StandardCharsets.UTF_8));
            return;
        }
        String name = file.getFileName().toString();
        String type = name.endsWith(".html") ? "text/html; charset=utf-8"
                : name.endsWith(".css") ? "text/css; charset=utf-8"
                : name.endsWith(".js") ? "application/javascript; charset=utf-8"
                : "application/octet-stream";
        send(ex, 200, type, Files.readAllBytes(file));
    }

    private void send(HttpExchange ex, int code, String type, byte[] body) throws IOException {
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(code, body.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(body);
        }
    }

    // ---------------------------------------------------------------- API
    private void handleApi(HttpExchange ex) throws IOException {
        Map<String, Object> resp = new LinkedHashMap<>();
        try {
            Map<String, String> p = params(ex);
            Object data;
            synchronized (this) {
                data = route(ex.getRequestURI().getPath(), p);
            }
            resp.put("ok", true);
            resp.put("data", data);
        } catch (ShopException e) {
            resp.put("ok", false);
            resp.put("error", e.getMessage());
        } catch (Exception e) {
            resp.put("ok", false);
            resp.put("error", "Server error: " + e);
        }
        send(ex, 200, "application/json; charset=utf-8", Json.toJson(resp).getBytes(StandardCharsets.UTF_8));
    }

    private Object route(String path, Map<String, String> p) throws ShopException {
        switch (path) {
            case "/api/health":
                return "OK";

            case "/api/register":
                return customers.register(str(p, "name"), str(p, "email"), str(p, "password"), str(p, "address")).toMap();

            case "/api/login":
                return customers.login(str(p, "email"), str(p, "password")).toMap();

            case "/api/products":
                return productMaps(products.search(str(p, "q"), str(p, "category")));

            case "/api/discounts": {
                List<Object> list = new ArrayList<>();
                for (Discount d : discounts.all()) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("code", d.getCode());
                    m.put("description", d.describe());
                    list.add(m);
                }
                return list;
            }

            case "/api/product/add": {
                admin(p);
                return products.add(optInt(p, "id"), req(p, "category"), req(p, "name"),
                        dbl(p, "price"), num(p, "stock"), str(p, "attribute")).toMap();
            }

            case "/api/product/update": {
                admin(p);
                return products.update(num(p, "id"), str(p, "name"), optDbl(p, "price"),
                        optInt(p, "stock"), str(p, "attribute")).toMap();
            }

            case "/api/product/delete": {
                admin(p);
                products.delete(num(p, "id"));
                return "Deleted";
            }

            case "/api/cart": {
                Customer c = customer(p);
                return carts.view(c.getCart());
            }

            case "/api/cart/add": {
                Customer c = customer(p);
                carts.add(c, num(p, "productId"), num(p, "quantity"));
                return carts.view(c.getCart());
            }

            case "/api/cart/remove": {
                Customer c = customer(p);
                carts.remove(c, num(p, "productId"));
                return carts.view(c.getCart());
            }

            case "/api/cart/update": {
                Customer c = customer(p);
                carts.updateQuantity(c, num(p, "productId"), num(p, "quantity"));
                return carts.view(c.getCart());
            }

            case "/api/cart/discount": {
                Customer c = customer(p);
                carts.applyDiscount(c, req(p, "code"));
                return carts.view(c.getCart());
            }

            case "/api/cart/discount/clear": {
                Customer c = customer(p);
                carts.clearDiscount(c);
                return carts.view(c.getCart());
            }

            case "/api/checkout": {
                Customer c = customer(p);
                return orders.checkout(c, req(p, "method"), p).toMap();
            }

            case "/api/orders": {
                if (!str(p, "customerId").isEmpty()) {
                    customer(p);
                    return orderMaps(orders.forCustomer(num(p, "customerId")));
                }
                admin(p);
                return orderMaps(orders.all());
            }

            case "/api/order/status": {
                Order.Status target;
                try {
                    target = Order.Status.valueOf(req(p, "status").toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new ShopException("Invalid status");
                }
                boolean byAdmin = !str(p, "adminId").isEmpty();
                int requester = 0;
                if (byAdmin) admin(p);
                else requester = customer(p).getId();
                return orders.changeStatus(num(p, "orderId"), target, byAdmin, requester).toMap();
            }

            case "/api/stats": {
                admin(p);
                return stats();
            }

            default:
                throw new ShopException("Unknown endpoint: " + path);
        }
    }

    private Map<String, Object> stats() {
        Map<String, Object> m = new LinkedHashMap<>();
        double revenue = 0;
        Map<String, Integer> byStatus = new LinkedHashMap<>();
        for (Order.Status s : Order.Status.values()) byStatus.put(s.name(), 0);
        Map<String, Double> byCategory = new LinkedHashMap<>();
        for (Order o : orders.all()) {
            byStatus.merge(o.getStatus().name(), 1, Integer::sum);
            if (o.getStatus() == Order.Status.CANCELLED) continue;
            revenue += o.getTotal();
            for (Order.Line l : o.getLines())
                byCategory.merge(l.category, l.unitPrice * l.quantity, Double::sum);
        }
        List<Object> low = new ArrayList<>();
        for (Product pr : products.all()) if (pr.getStock() <= 10) low.add(pr.toMap());
        m.put("totalProducts", products.all().size());
        m.put("totalCustomers", customers.customerCount());
        m.put("totalOrders", orders.all().size());
        m.put("revenue", revenue);
        m.put("ordersByStatus", byStatus);
        m.put("salesByCategory", byCategory);
        m.put("lowStock", low);
        return m;
    }

    // ---------------------------------------------------------------- helpers
    private Customer customer(Map<String, String> p) throws ShopException {
        return customers.getCustomer(num(p, "customerId"));
    }

    private void admin(Map<String, String> p) throws ShopException {
        customers.requireAdmin(num(p, "adminId"));
    }

    private List<Object> productMaps(Collection<Product> list) {
        List<Object> r = new ArrayList<>();
        for (Product pr : list) r.add(pr.toMap());
        return r;
    }

    private List<Object> orderMaps(List<Order> list) {
        List<Object> r = new ArrayList<>();
        for (Order o : list) r.add(o.toMap());
        return r;
    }

    private Map<String, String> params(HttpExchange ex) throws IOException {
        Map<String, String> map = new HashMap<>();
        parseInto(map, ex.getRequestURI().getRawQuery());
        parseInto(map, new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
        return map;
    }

    private void parseInto(Map<String, String> map, String s) {
        if (s == null || s.isBlank()) return;
        for (String pair : s.split("&")) {
            int i = pair.indexOf('=');
            String k = i < 0 ? pair : pair.substring(0, i);
            String v = i < 0 ? "" : pair.substring(i + 1);
            map.put(URLDecoder.decode(k, StandardCharsets.UTF_8), URLDecoder.decode(v, StandardCharsets.UTF_8));
        }
    }

    private String str(Map<String, String> p, String k) {
        String v = p.get(k);
        return v == null ? "" : v.trim();
    }

    private String req(Map<String, String> p, String k) throws ShopException {
        String v = str(p, k);
        if (v.isEmpty()) throw new ShopException(k + " is required");
        return v;
    }

    private int num(Map<String, String> p, String k) throws ShopException {
        try {
            return Integer.parseInt(req(p, k));
        } catch (NumberFormatException e) {
            throw new ShopException(k + " must be a whole number");
        }
    }

    private double dbl(Map<String, String> p, String k) throws ShopException {
        try {
            return Double.parseDouble(req(p, k));
        } catch (NumberFormatException e) {
            throw new ShopException(k + " must be a number");
        }
    }

    private Integer optInt(Map<String, String> p, String k) throws ShopException {
        return str(p, k).isEmpty() ? null : Integer.valueOf(num(p, k));
    }

    private Double optDbl(Map<String, String> p, String k) throws ShopException {
        return str(p, k).isEmpty() ? null : Double.valueOf(dbl(p, k));
    }
}