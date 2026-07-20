# ==============================================================================
# STAGE 1: Build Phase
# ==============================================================================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copy pom.xml first (cache dependencies)
COPY pom.xml .

# Download dependencies (optional cache step)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# BUILD REAL DEL JAR (ESTO FALTABA)
RUN mvn clean package -DskipTests -B

# Verificación opcional (recomendado para debug)
RUN ls -l target


# ==============================================================================
# STAGE 2: Runtime Phase
# ==============================================================================
FROM eclipse-temurin:17-jre-alpine AS runner

WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

# Create uploads folder
RUN mkdir -p /app/uploads && chown -R spring:spring /app/uploads

USER spring:spring

# Copiar JAR desde builder
COPY --from=builder /app/target/*.jar app.jar

# Verificación opcional (si quieres debug)
# RUN ls -l /app

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -XX:+UseStringDeduplication"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

#mensaje de prueba
