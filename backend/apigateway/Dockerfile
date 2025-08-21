FROM gradle:8-jdk17-ubi AS dev-builder
WORKDIR /app
COPY . .
RUN cat build.gradle.kts
RUN gradle build --no-daemon

FROM gradle:8-jdk17-ubi AS prod-builder
WORKDIR /app
COPY . .
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
