E-COMMERCE MANAGEMENT SYSTEM (ShopEasy)
Core Java project

ABOUT
A Core Java e-commerce application with a web interface. Customers can
register, log in, search products, manage a cart, apply discounts, pay by
UPI / Card / Cash on Delivery, and view orders. The admin can add, update,
search and remove products, manage stock and price, and view and update
orders (ship, deliver, cancel).

OOP CONCEPTS USED
Classes and objects, inheritance (User -> Customer/Admin,
Product -> Electronics/Clothing/Grocery), encapsulation, abstraction,
polymorphism (UPI/Card/COD payments), interfaces (Discount), collections
(ArrayList, HashMap, LinkedHashMap), custom exception handling,
constructors and static members.

REQUIREMENTS
JDK 17 or newer (JDK 21 used). Check with:  java -version

HOW TO RUN (WINDOWS)
1. Unzip the folder.
2. Double-click run.bat
3. The browser opens at http://localhost:8080
   (If it does not, open http://localhost:8080 manually.)
4. To stop the server, close the black window or press Ctrl+C.

HOW TO RUN (MANUAL, ANY SYSTEM)
In a terminal inside the project folder:
  Windows:    dir /s /b src\*.java > sources.txt
              javac -d out @sources.txt
  Mac/Linux:  javac -d out $(find src -name "*.java")
Then:
  java -cp out com.besant.ecommerce.main.ECommerceApplication
Open http://localhost:8080

LOGIN DETAILS
Admin:     admin@shop.com / admin123
Customer:  click Register on the login page and create an account.
Discount codes: SAVE10, BIG20, FLAT500
Demo payment: any UPI ID like john@upi, or any 16-digit card number.

DATABASE
This version does not use a database (as per the project document).
All data is stored in Java memory using collections, so data is reset
when the application is stopped. Admin login and 12 sample products are
loaded automatically at every start.

FOLDER STRUCTURE
src/com/besant/ecommerce/
    exception/  custom exceptions
    model/      User, Customer, Admin, Product, Electronics, Clothing,
                Grocery, Cart, CartItem, Order
    discount/   Discount interface and offers
    payment/    Payment, UpiPayment, CardPayment, CashPayment
    service/    Product, Customer, Cart, Order, Payment, Discount services
    main/       ECommerceApplication, ApiServer, Json
web/index.html  user interface