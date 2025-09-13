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
ENV JPDA_ADDRESS="0.0.0.0:5005" \
    JPDA_TRANSPORT="dt_socket" \
    JPDA_SUSPEND="n"

# Debug: Ausgabe der Umgebungsvariablen und Dateistruktur
RUN echo "=== DEBUG: ENV-Variablen vor Start ===" && \
    echo "JPDA_ADDRESS: $JPDA_ADDRESS" && \
    echo "JPDA_TRANSPORT: $JPDA_TRANSPORT" && \
    echo "JPDA_SUSPEND: $JPDA_SUSPEND" && \
    echo "CATALINA_HOME: $CATALINA_HOME" && \
    echo "=== DEBUG: Webapps-Verzeichnis ===" && \
    ls -la /usr/local/tomee/webapps/ && \
    echo "=== DEBUG: Netzwerk-Interfaces (vor Start) ===" && \
    ip addr show || true && \
    echo "=== DEBUG: Port-Check (netstat falls verfügbar) ===" && \
    (netstat -tlnp || ss -tlnp || echo "netstat/ss nicht verfügbar") || true && \
    echo "=== DEBUG: Host-Auflösung testen ===" && \
    nslookup 0.0.0.0 || echo "nslookup nicht verfügbar" && \
    getent hosts 0.0.0.0 || echo "getent nicht verfügbar" && \
    echo "=== DEBUG: TomEE-Konfig-Dateien ===" && \
    find /usr/local/tomee -name "*.sh" -o -name "catalina*" | head -10 && \
    echo "=== DEBUG: Build abgeschlossen ==="

# Expose application port and debug port
EXPOSE 8080 5005

# Debug: Zusätzliche Ausgabe beim CMD-Start (wird beim Container-Start ausgeführt)
RUN echo "=== DEBUG: CMD wird ausgeführt: catalina.sh run ===" > /tmp/start-debug.log && \
    echo "Überprüfe /tmp/start-debug.log nach dem Start für weitere Infos."

# Default command (overridden by docker-compose for debug)
CMD ["sh", "-c", "echo '=== DEBUG: Container startet mit catalina.sh run ==='; echo 'Aktuelle ENV: JPDA_ADDRESS=$JPDA_ADDRESS'; ls -la /usr/local/tomee/webapps/; catalina.sh run 2>&1 | tee /tmp/catalina-debug.log"]
