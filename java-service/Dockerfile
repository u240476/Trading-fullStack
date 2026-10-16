FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY java-service/pom.xml .
RUN mvn -B -q dependency:go-offline
COPY java-service/src ./src
RUN mvn -B -q package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd -r -u 1001 appuser
COPY --from=build /build/target/*.jar app.jar
USER appuser
EXPOSE 8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]