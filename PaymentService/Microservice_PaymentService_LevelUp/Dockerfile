# --- Etapa 1: Build con Maven + Java 21 ---
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copiamos solo pom primero para aprovechar cache
COPY pom.xml ./

# Descargamos dependencias
RUN mvn -B dependency:go-offline

# Ahora sí, copiamos el código
COPY src ./src

# Compilamos y empaquetamos (sin tests para acelerar)
RUN mvn -B clean package -DskipTests

# --- Etapa 2: Runtime ligero con JRE 21 ---
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiamos el jar generado desde la etapa de build
COPY --from=build /app/target/payment-service-0.0.1-SNAPSHOT.jar app.jar

# Puerto interno del microservicio (coincide con server.port=8083)
EXPOSE 8083

# Opcional: flags extra de JVM
ENV JAVA_OPTS=""

# Entrada del contenedor
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
