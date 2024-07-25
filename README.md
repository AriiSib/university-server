[![codecov](https://codecov.io/gh/ariiSib/university-server/branch/dev/graph/badge.svg)](https://codecov.io/gh/ariiSib/university-server)
[![Java CI with Gradle](https://github.com/AriiSib/university-server/actions/workflows/gradle.yml/badge.svg?branch=dev)](https://github.com/AriiSib/university-server/actions/workflows/gradle.yml)

# University Server

## Description

**University Server** is a REST web application built using the Java Servlet API, which serves as a backend component
for managing student data, groups, and schedules in a university system. The project includes the use of JSP, JSTL, and
Servlets.

## Features

- Manage student data, groups, and schedules via REST API, for example, using Postman.
- Use JUnit and Mockito for testing application logic.
- Logging with Logback and SLF4J.
- Integration with Docker for easy deployment.
- Continuous integration with [GitHub Actions](https://github.com/AriiSib/university-server/actions)
  and [Codecov](https://app.codecov.io/gh/ariiSib/university-server) for code coverage tracking.

## Stack

- **Java 21**
- **Tomcat 10.1.24**
- **Gradle 8.7**
- **JSP/JSTL**
- **JUnit 5**
- **Mockito**
- **Jacoco**
- **Jackson**
- **Logback**
- **Docker**
- **GitHub Actions**

## Testing with Postman

You can test the API functionality using Postman. Click the button below to import the collection:

[![Run in Postman](https://run.pstmn.io/button.svg)](https://god.gw.postman.com/run-collection/35343974-fc36830f-0e1a-4336-aafa-d22ed0b37080?action=collection%2Ffork&source=rip_markdown&collection-url=entityId%3D35343974-fc36830f-0e1a-4336-aafa-d22ed0b37080%26entityType%3Dcollection%26workspaceId%3D27ed7783-1773-466a-9f35-f723637494d0)

Or

You can import the collection into Postman. Click the button below to download the collection:

[![Download Collection](https://img.shields.io/badge/Download_Collection-JSON-blue)](https://github.com/AriiSib/university-server/blob/dev/University%20REST%20Application.postman_collection.json)

## Docker Image

`docker pull ariisib/university-server:latest`

# Installation

## Option 1: Using Docker

1. Pull the Docker image from Docker Hub:
    ```sh
    docker pull ariisib/university-server:latest
    ```

2. Run the container:
    ```sh
    docker run -d -p 8080:8080 --name university-server ariisib/university-server:latest
    ```

   The application will be available
   at [http://localhost:8080/university-server/](http://localhost:8080/university-server/)

## Option 2: Local Deployment via IDE and Tomcat

1. Ensure that Apache Tomcat 10.1.24, JDK 21 and Gradle are installed.

2. Import the project into your IDE.

3. Build the WAR file:
    ```sh
    gradle clean build
    ```

4. Configure Tomcat in your IDE and specify the project path:
    - Application context: `/university-server`
    - Port: `8080`

5. Deploy the WAR file:
    - Copy the generated `university-server.war` from `build/libs` to the `webapps` directory of your Tomcat
      installation.

6. Start the Tomcat server in your IDE.

   The application will be available
   at [http://localhost:8080/university-server/](http://localhost:8080/university-server/)