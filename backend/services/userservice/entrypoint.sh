#! /bin/sh

curl -k https://repository.mostserene.eu/avogador/artifacts/userservice.jar -u ${REPOSITORY_CREDENTIALS}  --output userservice.jar
java -jar ./userservice.jar