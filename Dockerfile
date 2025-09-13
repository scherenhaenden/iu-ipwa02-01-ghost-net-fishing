# --- Stage 1: FULLY DOCKERIZED MAVEN BUILD ---
FROM --platform=linux/amd64 maven:3.8.8-eclipse-temurin-8 AS build
WORKDIR /app

# Install unzip for WAR analysis inside the container
RUN apt-get update -qq && apt-get install -y unzip && rm -rf /var/lib/apt/lists/*

# Copy pom.xml first for dependency caching (all inside container)
COPY pom.xml .

# Download ALL dependencies inside container (no local Maven needed)
RUN mvn -B -q -DskipTests dependency:resolve

# Copy source code and build ENTIRE application inside container
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# Verify the WAR was created inside container
RUN ls -la /app/target/ && test -f /app/target/iu-ipwa02-01-ghost-net-fishing.war

# DETAILED WAR ANALYSIS - All inside container
RUN echo "=== DOCKERIZED WAR ANALYSIS ===" && \
    echo "WAR size: $(wc -c < /app/target/iu-ipwa02-01-ghost-net-fishing.war) bytes" && \
    echo "=== ROOT LEVEL FILES ===" && \
    unzip -l /app/target/iu-ipwa02-01-ghost-net-fishing.war | grep -E "^\s+[0-9]+\s+.*(index\.xhtml|index\.jsp)$" || echo "NO index file in root!" && \
    echo "=== ALL XHTML FILES ===" && \
    unzip -l /app/target/iu-ipwa02-01-ghost-net-fishing.war | grep "\.xhtml" && \
    echo "=== WELCOME FILE CONFIG ===" && \
    unzip -p /app/target/iu-ipwa02-01-ghost-net-fishing.war WEB-INF/web.xml | grep -A5 "<welcome-file-list>" || echo "No welcome-file-list found"

# --- Stage 2: FULLY DOCKERIZED TOMEE DEPLOYMENT ---
FROM --platform=linux/amd64 tomee:8-jre-7.0.5-webprofile

# Update package sources for old Debian inside container
USER root
RUN sed -i 's/deb.debian.org/archive.debian.org/g' /etc/apt/sources.list && \
    sed -i 's/security.debian.org/archive.debian.org/g' /etc/apt/sources.list && \
    sed -i '/buster-updates/d' /etc/apt/sources.list && \
    apt-get update -qq && \
    apt-get install -y curl unzip && \
    rm -rf /var/lib/apt/lists/*

# **SIMPLE CLEANUP - Just remove webapps, NO xmllint validation**
RUN echo "=== DOCKERIZED CLEANUP OF DEFAULT WEBAPPS ===" && \
    echo "=== INITIAL WEBAPPS STATE ===" && \
    find /usr/local/tomee/webapps -mindepth 1 -ls && \
    echo "=== REMOVING DEFAULT WEBAPPS (KEEPING CONFIG) ===" && \
    rm -rf /usr/local/tomee/webapps/* && \
    echo "=== CLEAN WEBAPPS DIRECTORY ===" && \
    ls -la /usr/local/tomee/webapps/ && \
    echo "=== SERVER.XML PRESERVED (size: $(wc -c < /usr/local/tomee/conf/server.xml) bytes) ===" && \
    echo "✓ Webapps cleaned - ready for your application"

# **COPY YOUR BUILT WAR** - Fresh from Maven stage
COPY --from=build /app/target/iu-ipwa02-01-ghost-net-fishing.war \
  /usr/local/tomee/webapps/ROOT.war

# **FINAL VERIFICATION** - Your app is ready to deploy
RUN echo "=== YOUR GHOST NET FISHING APP READY ===" && \
    ls -la /usr/local/tomee/webapps/ && \
    echo "Your WAR size: $(wc -c < /usr/local/tomee/webapps/ROOT.war) bytes" && \
    echo "WAR contains index.xhtml:" && \
    unzip -l /usr/local/tomee/webapps/ROOT.war | grep "index.xhtml" && \
    echo "Files in webapps (should be 1): $(ls -1 /usr/local/tomee/webapps/ | grep -v '^\.$' | wc -l)" && \
    echo "✓ SUCCESS: Only your ROOT.war - no default apps"

# Expose port
EXPOSE 8080

# JVM settings optimized for your JSF app
ENV JAVA_OPTS="-Xmx512m -Dcom.sun.faces.enableLazyBeanValidation=false"
ENV CATALINA_OPTS="-Dorg.apache.catalina.level=INFO -Dorg.apache.openejb.level=INFO"

# **SIMPLE STARTUP SCRIPT** - Clean and reliable
RUN echo '#!/bin/bash' > /start.sh && \
    echo 'set -e' >> /start.sh && \
    echo 'echo "=== STARTING GHOST NET FISHING APP ==="' >> /start.sh && \
    echo 'echo "=== Final webapps check ==="' >> /start.sh && \
    echo 'ls -la /usr/local/tomee/webapps/' >> /start.sh && \
    echo 'echo "=== Starting TomEE (your app only) ==="' >> /start.sh && \
    echo 'exec /usr/local/tomee/bin/catalina.sh run' >> /start.sh && \
    chmod +x /start.sh

# Run your dockerized application
CMD ["/start.sh"]