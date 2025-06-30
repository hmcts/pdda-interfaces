 # renovate: datasource=github-releases depName=microsoft/ApplicationInsights-Java
 # Use Tomcat base image
 FROM tomcat:9.0-jdk21-temurin

 # Set Application Insights version
 ARG APP_INSIGHTS_AGENT_VERSION=3.7.3

 # Create agent directory
 RUN mkdir -p /opt/appinsights

 # Download Application Insights Java agent
 ADD https://github.com/microsoft/ApplicationInsights-Java/releases/download/${APP_INSIGHTS_AGENT_VERSION}/applicationinsights-agent-${APP_INSIGHTS_AGENT_VERSION}.jar \
     /opt/appinsights/applicationinsights-agent.jar

 # Copy your Application Insights config file
 COPY lib/applicationinsights.json /opt/app/

 # Copy your WAR to Tomcat's webapps directory as ROOT
 COPY build/libs/PDDA-1.0.war /usr/local/tomcat/webapps/ROOT.war

 # Expose port (Tomcat default is 8080)
 EXPOSE 8080

 # Override the Tomcat startup command to include the App Insights agent
 CMD ["catalina.sh", "run"]
 ENV JAVA_TOOL_OPTIONS="-javaagent:/opt/appinsights/applicationinsights-agent.jar"
