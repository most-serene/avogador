#! /bin/sh

curl -k https://repository.mostserene.eu/avogador/artifacts/courseservice.jar -u ${REPOSITORY_CREDENTIALS} --output courseservice.jar
java -jar ./courseservice.jar