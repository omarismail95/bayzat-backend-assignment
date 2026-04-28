FROM gradle:8.5-jdk17-alpine AS builder

WORKDIR /app

COPY . .

RUN gradle bootJar -x test

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /app/build/libs/bayz-delivery-1.0.0.jar app.jar

USER appuser

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
