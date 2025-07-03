FROM gradle:8.14.2 AS builder
WORKDIR /application
COPY . .
RUN --mount=type=cache,target=/root/.gradle gradle clean :statistic-service:build -x test

FROM amazoncorretto:21-alpine AS layers
WORKDIR /application
COPY --from=builder /application/statistic-service/build/libs/*.jar app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted

FROM amazoncorretto:21-alpine
VOLUME /tmp

WORKDIR /application

COPY --from=layers /application/extracted/dependencies/ ./
COPY --from=layers /application/extracted/spring-boot-loader/ ./
COPY --from=layers /application/extracted/snapshot-dependencies/ ./
COPY --from=layers /application/extracted/application/ ./

ENV JAVA_ERROR_FILE_OPTS="-XX:ErrorFile=/tmp/java_error.log"

ENTRYPOINT java \
    $JAVA_ERROR_FILE_OPTS \
    -jar app.jar
