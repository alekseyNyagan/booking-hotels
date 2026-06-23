# === Stage 1: Сборка ===
FROM gradle:9.5.1-jdk-25-and-26-alpine AS builder
WORKDIR /app

# Копируем глобальную инфраструктуру Gradle для кэширования слоев
COPY gradle/ gradle/
COPY build.gradle.kts settings.gradle.kts gradlew ./

# Копируем конфигурационные файлы подмодулей для кэширования зависимостей
COPY booking-hotels-service/build.gradle.kts ./booking-hotels-service/
COPY user-service/build.gradle.kts ./user-service/
COPY statistic-service/build.gradle.kts ./statistic-service/
COPY config-server/build.gradle.kts ./config-server/
COPY eureka-server/build.gradle.kts ./eureka-server/
COPY gateway-server/build.gradle.kts ./gateway-server/

# Скачиваем зависимости (этот слой закешируется)
RUN ./gradlew dependencies --no-daemon

# Копируем исходный код
COPY . .

# Принимаем аргумент с именем модуля и собираем bootJar
ARG MODULE_NAME
RUN ./gradlew :${MODULE_NAME}:bootJar --no-daemon

# === Stage 2: Извлечение слоев Spring Boot ===
FROM eclipse-temurin:25-jre-alpine AS layers
WORKDIR /app
ARG MODULE_NAME

COPY --from=builder /app/${MODULE_NAME}/build/libs/*.jar app.jar

RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted

# === Stage 3: Легковесный Runtime ===
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Безопасность: запуск от не-привилегированного пользователя
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring


COPY --from=layers /app/extracted/dependencies/ ./
COPY --from=layers /app/extracted/spring-boot-loader/ ./
COPY --from=layers /app/extracted/snapshot-dependencies/ ./
COPY --from=layers /app/extracted/application/ ./

# Оптимизация запуска Java
ENTRYPOINT ["java", "-XX:+UseParallelGC", "-jar", "app.jar"]
