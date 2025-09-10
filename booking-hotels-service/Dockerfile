FROM gradle:8.14.2 AS builder
WORKDIR /application
COPY . .
RUN --mount=type=cache,target=/root/.gradle gradle clean :booking-hotels-service:build -x test

FROM amazoncorretto:21-alpine
VOLUME /tmp

WORKDIR /application

COPY --from=builder /application/booking-hotels-service/build/libs/*.jar app.jar

ENV JAVA_ERROR_FILE_OPTS="-XX:ErrorFile=/tmp/java_error.log"

ENTRYPOINT java \
$JAVA_ERROR_FILE_OPTS \
-jar app.jar
