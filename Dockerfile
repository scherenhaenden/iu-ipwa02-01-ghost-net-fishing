# --- Stage 1: Build WAR ---
FROM --platform=linux/amd64 maven:3.8.8-eclipse-temurin-8 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# --- Stage 2: Runtime TomEE EE7 ---
FROM --platform=linux/amd64 tomee:8-jre-7.0.5-webprofile
COPY --from=build /app/target/iu-ipwa02-01-ghost-net-fishing.war /usr/local/tomee/webapps/ROOT.war
