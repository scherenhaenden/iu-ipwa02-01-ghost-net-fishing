# --- Stage 1: build the WAR inside the container ---
FROM --platform=linux/amd64 maven:3.8.8-eclipse-temurin-8 AS build
WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .

# Resolve dependencies (cache layer)
RUN mvn -B -q -DskipTests dependency:resolve

# Copy source code and build
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# Verify the WAR was created
RUN ls -la /app/target/ && test -f /app/target/iu-ipwa02-01-ghost-net-fishing.war

# --- Stage 2: run on TomEE 7 (Java EE 7) with debug support ---
FROM --platform=linux/amd64 tomee:8-jre-7.0.5-webprofile

# Copy the WAR file to TomEE webapps (rename to ROOT.war for root context)
COPY --from=build /app/target/iu-ipwa02-01-ghost-net-fishing.war \
  /usr/local/tomee/webapps/ROOT.war

# Configure JPDA for remote debugging (bind to all interfaces)
ENV JPDA_ADDRESS="0.0.0.0:5005" JPDA_TRANSPORT="dt_socket" JPDA_SUSPEND="n"

# Expose application port and debug port
EXPOSE 8080 5005

# Default command (overridden by docker-compose for debug)
CMD ["catalina.sh", "jpda", "run"]
