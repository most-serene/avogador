FROM gradle:jdk17 AS base
WORKDIR /app
COPY . .

FROM base AS dev-builder
RUN gradle build --no-daemon

FROM base AS prod-builder
RUN gradle assemble --no-daemon

FROM scratch AS reporter
COPY --from=base build/reports/tests /reports

###

FROM openjdk AS run
ARG JAR_FILE=/app/build/libs/*.jar
COPY --from=dev-builder ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

FROM openjdk AS prod-run
ARG JAR_FILE=/app/build/libs/*.jar
COPY --from=prod-builder ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
