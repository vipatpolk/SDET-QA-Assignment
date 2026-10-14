FROM mcr.microsoft.com/playwright/java:v1.50.0-noble

WORKDIR /app

# 1. Copy Maven Wrapper and POM first to leverage Docker layer caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Ensure Linux execute permissions and sanitize Windows CRLF line endings
RUN chmod +x mvnw && sed -i 's/\r$//' mvnw

# 2. Pre-fetch dependencies to speed up subsequent test executions
RUN ./mvnw dependency:go-offline -B || true

# 3. Copy application & test sources and entrypoint script
COPY src/ src/
COPY docker-entrypoint.sh ./
RUN chmod +x docker-entrypoint.sh && sed -i 's/\r$//' docker-entrypoint.sh

# 4. Entrypoint and default command
ENTRYPOINT ["./docker-entrypoint.sh"]
CMD ["verify"]
