FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY messaging-contracts/pom.xml messaging-contracts/pom.xml
COPY backend/pom.xml backend/pom.xml
COPY transaction-service/pom.xml transaction-service/pom.xml
RUN mvn -pl backend -am dependency:go-offline -DskipTests
COPY messaging-contracts/src messaging-contracts/src
COPY backend/src backend/src
RUN mvn -pl backend -am package -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /workspace/backend/target/monolith-*.jar app.jar
USER app
EXPOSE 8080
HEALTHCHECK --interval=10s --timeout=5s --start-period=20s --retries=6 \
  CMD wget -qO- http://localhost:8080/actuator/health/readiness || exit 1
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
