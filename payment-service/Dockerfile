FROM eclipse-temurin:21
COPY target/lib /usr/src/lib
COPY target/payment-service-1.0.0.jar /usr/src/
WORKDIR /usr/src/
CMD java -Xmx32m -jar payment-service-1.0.0.jar
