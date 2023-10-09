#! /bin/sh

curl -k https://repository.mostserene.eu/avogador/artifacts/executor.jar -u ${REPOSITORY_CREDENTIALS} --output executor.jar
java -jar ./executor.jar