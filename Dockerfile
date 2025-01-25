FROM gradle:8.11.1-jdk21 AS builder
#Gradle 8.11.1
COPY . /usr/src
WORKDIR /usr/src
RUN gradle wrapper --gradle-version 8.11.1
RUN ./gradlew clean build -x test

FROM openjdk:21-jdk
#debian기반
COPY --from=builder /usr/src/build/libs/learningFlow-BE-0.0.1-SNAPSHOT.jar /usr/app/app.jar
ENTRYPOINT ["java", "-jar", "/usr/app/app.jar"]