package com.besant.ecommerce.main;

import java.nio.file.Paths;

public class ECommerceApplication {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        String env = System.getenv("PORT");          // hosting platforms set this
        if (env != null && !env.isBlank()) port = Integer.parseInt(env.trim());
        new ApiServer(Paths.get("web")).start(port);
    }
}