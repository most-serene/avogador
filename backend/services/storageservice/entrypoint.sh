#! /bin/sh

curl -k https://repository.mostserene.eu/avogador/artifacts/storageservice.jar -u ${REPOSITORY_CREDENTIALS}  --output storageservice.jar
java -jar ./storageservice.jar