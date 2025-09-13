# Dockerfile (arm64-fähig)
FROM tomee:8-jre-8.0.16-webprofile
COPY target/iu-ipwa02-01-ghost-net-fishing.war /usr/local/tomee/webapps/ROOT.war
