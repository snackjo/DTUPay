FROM eclipse-temurin:21
COPY target/lib /usr/src/lib
COPY target/token-service-1.0.0-runner.jar /usr/src/
WORKDIR /usr/src/
CMD java -Xmx64m -jar token-service-1.0.0-runner.jar
