#! /bin/sh

curl -k https://repository.mostserene.eu/avogador/artifacts/filesystemservice.jar -u ${REPOSITORY_CREDENTIALS}  --output filesystemservice.jar
java -jar ./filesystemservice.jar