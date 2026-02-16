FROM eclipse-temurin:8-jdk AS builder
WORKDIR /build
RUN apt-get update && apt-get install -y --no-install-recommends maven && rm -rf /var/lib/apt/lists/*
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:8-jre
WORKDIR /app
RUN mkdir -p /app/data
COPY --from=builder /build/target/*.jar app.jar
EXPOSE 8080
ENV SPRING_DATASOURCE_URL=jdbc:h2:file:/app/data/BDSB_H2;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
ENTRYPOINT ["java", "-jar", "app.jar"]
