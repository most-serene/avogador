FROM gradle:7.5-jdk17-focal AS base
WORKDIR /app

#COPY build.gradle.kts settings.gradle.kts /app/
#COPY gradlew gradlew.bat /app/
#COPY gradle /app/gradle

# RUN gradle clean assemble --no-daemon > /dev/null 2>&1 || true

#FROM base AS test

#COPY . .
#ENTRYPOINT ["gradle", "test", "--info"]

#FROM base AS build

COPY . .
RUN gradle clean assemble --no-daemon

FROM openjdk AS run
ARG JAR_FILE=/app/build/libs/*.jar
COPY --from=base ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
