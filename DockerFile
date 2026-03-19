# 1. Aşama: Kodunu derlemek için Maven ve Java içeren bir imaj kullanıyoruz
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# 2. Aşama: Sadece JAR dosyasını çalıştıracak hafif bir imaj
FROM openjdk:17-jdk-slim
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]