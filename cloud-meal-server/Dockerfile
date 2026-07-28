FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/target/cloud-meal-server-*.jar app.jar
ENV TZ=Asia/Shanghai
EXPOSE 8080
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","app.jar"]
