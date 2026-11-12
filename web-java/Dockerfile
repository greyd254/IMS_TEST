# Build jar in a Maven container -> khong can cai Maven tren may local
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B -q dependency:go-offline
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -q clean package -DskipTests

# Run-time image chi can JRE 21
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/target/suco-web-java-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
