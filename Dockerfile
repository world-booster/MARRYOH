FROM eclipse-temurin:17-jre-alpine

RUN apk add --no-cache curl

WORKDIR /svc/marryoh

COPY build/libs/*.jar app.jar

EXPOSE 8181

ENTRYPOINT ["java", "-jar", "/svc/marryoh/app.jar"]