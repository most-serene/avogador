FROM gradle:jdk17 as base
WORKDIR /app
# Copy the application source code
COPY . .
# Build the application and execute unit tests (build task depends on test task)
RUN ./gradlew build javadoc --no-daemon


FROM scratch AS reporter
COPY --from=base build/reports/tests /reports


FROM openjdk AS run
ARG JAR_FILE=/app/build/libs/*.jar
COPY --from=base ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
