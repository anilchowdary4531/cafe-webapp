# ---- Build stage ----
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN apk add --no-cache maven && \
    mvn -B -DskipTests package

# ---- Run stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S cafe && adduser -S cafe -G cafe

COPY --from=build /app/target/*.jar app.jar

RUN chown cafe:cafe app.jar
USER cafe

EXPOSE 4100

ENV JAVA_OPTS="-Xms128m -Xmx384m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

