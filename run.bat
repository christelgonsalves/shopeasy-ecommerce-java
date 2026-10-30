@echo off
echo Compiling ShopEasy...
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
echo Starting server at http://localhost:8080
start http://localhost:8080
java -cp out com.besant.ecommerce.main.ECommerceApplication
pause