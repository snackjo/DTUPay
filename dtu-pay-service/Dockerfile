FROM eclipse-temurin:21
COPY target/lib /usr/src/lib
COPY target/dtu-pay-service-1.0.0-runner.jar /usr/src/
WORKDIR /usr/src/
CMD java -Xmx64m -jar dtu-pay-service-1.0.0-runner.jar
