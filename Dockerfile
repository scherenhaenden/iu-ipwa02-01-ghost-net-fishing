# Java EE 7 Web Profile (javax.*), Java 8
FROM tomee:8-jre-7.0.5-webprofile
COPY target/iu-ipwa02-01-ghost-net-fishing.war /usr/local/tomee/webapps/ROOT.war
