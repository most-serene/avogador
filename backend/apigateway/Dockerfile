FROM gradle:jdk17 as dev-builder2
WORKDIR /app
COPY . .
RUN cat build.gradle.kts
RUN gradle build --no-daemon

FROM gradle:jdk17 as prod-builder
WORKDIR /app
COPY . .
RUN gradle assemble --no-daemon

FROM scratch AS reporter
COPY --from=base build/reports/tests /reports

###

FROM openjdk AS run
ARG JAR_FILE=/app/build/libs/*.jar
COPY --from=dev-builder2 ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

FROM openjdk AS prod-run
ARG JAR_FILE=/app/build/libs/*.jar
COPY --from=prod-builder ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
