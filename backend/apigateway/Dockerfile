FROM gradle:8-jdk17-ubi AS installer
WORKDIR /app
COPY settings.gradle.kts build.gradle.kts ./
RUN gradle clean build --no-daemon  > /dev/null 2>&1 || true

FROM installer AS base 
COPY . .

FROM base AS dev-builder
RUN gradle build --no-daemon

FROM base AS prod-builder
RUN gradle assemble --no-daemon

FROM scratch AS reporter
COPY --from=dev-builder build/reports/tests /reports

###

FROM openjdk AS run
ARG JAR_FILE=/app/build/libs/*.jar
COPY --from=dev-builder ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

FROM openjdk AS prod-run
ARG JAR_FILE=/app/build/libs/*.jar
COPY --from=prod-builder ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
