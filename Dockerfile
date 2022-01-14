FROM adoptopenjdk/openjdk11:alpine-jre
COPY target/hiringApp.jar hiringApp.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/hiringApp.jar"]