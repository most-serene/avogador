#! /bin/sh

curl -k https://repository.mostserene.eu/avogador/artifacts/executorservice.jar -u ${REPOSITORY_CREDENTIALS} --output executorservice.jar
java -jar ./executorservice.jar