FROM openjdk:8-jdk-alpine
WORKDIR /root/
RUN apk add git \
		&& git clone https://github.com/XFNeo/departments-management-ws.git \
		&& cd /root/departments-management-ws \
		&& chmod +x mvnw \
		&& ./mvnw package \
		&& cp target/departments-management*.jar ./app.jar
EXPOSE 8080
ENTRYPOINT  [ "java", "-jar", "/root/departments-management-ws/app.jar" ]
