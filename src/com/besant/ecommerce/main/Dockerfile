FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY . .
RUN mkdir -p out && javac -d out $(find src -name "*.java")
CMD ["java", "-cp", "out", "com.besant.ecommerce.main.ECommerceApplication"]