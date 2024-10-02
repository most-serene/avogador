FROM gradle:jdk17 as base
WORKDIR /app
COPY . .

FROM base as dev-builder
RUN gradle build

FROM base as prod-builder
RUN gradle assemble

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
