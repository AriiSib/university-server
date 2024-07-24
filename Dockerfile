FROM tomcat:10.1.24-jdk21

WORKDIR /usr/local/tomcat

COPY build/libs/university_server.war webapps/university_server.war

EXPOSE 8080

CMD ["catalina.sh", "run"]