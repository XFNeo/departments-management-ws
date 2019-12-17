# departments-management-ws
Simple Spring Boot REST web service for departments management.  
This service depends on [employee-managment service](https://github.com/XFNeo/employees-management-ws).  
Swagger user interface available on "/swagger-ui.html"  
Container with application automated builds on [Docker hub](https://hub.docker.com/r/xfneo/departments-management-ws).

## Prerequisites
 - PostgreSQL 11 with database "departments_service"
 - JDK 8
 - Launched [employee-managment service](https://github.com/XFNeo/employees-management-ws)
 - Docker and docker-compose for container deploy
 
### Environment variables:  
- DB_USERNAME - username for database. Default: postgres
- DB_PASSWORD - password for database. Default: postgres
- DB_HOST - database host. Default: localhost
- DB_PORT - database port. Default: 5432
- DEPT_APP_PORT - application port for api. Default: 8080
- EMPLOYEES_SERVICE_URL - URL and port to [employee-managment service](https://github.com/XFNeo/employees-management-ws). Default:  http://localhost:8080

## Deploy application:
### Linux:
Prepare all necessary environment variables and run commands:
```sh
chmod +x mvnw
./mvnw package
cp target/departments-management*.jar ./app.jar
java -jar app.jar
```
### Windows:
Prepare all necessary environment variables and run commands:
```cmd
mvnw.cmd package
copy /B target\departments-management*.jar app.jar
java -jar app.jar
```

## Deploy application with docker:
Run the command (don't forget to change necessary environment variables and prepare database):
```sh
docker run --name dept_app -p 80:8080 -e DB_USERNAME=postgres -e DB_PASSWORD=postgres -e DB_HOST=localhost -e DB_PORT=5432 -e EMPLOYEES_SERVICE_URL=http://localhost:9090 xfneo/employees-management-ws:latest
```

## Deploy applications (departments-management-ws and employees-management-ws) and databases with docker-compose:
Go to directory with docker-compose.yml file and run the command:
```sh
docker-compose up
```
