FROM tomcat:10.1.24-jdk21

WORKDIR /usr/local/tomcat

COPY build/libs/university-server-1.0.0.war webapps/university-server.war

EXPOSE 8080

CMD ["catalina.sh", "run"]