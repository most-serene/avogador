#! /bin/sh

curl -k https://repository.mostserene.eu/avogador/artifacts/exerciseservice.jar -u ${REPOSITORY_CREDENTIALS} --output exerciseservice.jar
java -jar ./exerciseservice.jar