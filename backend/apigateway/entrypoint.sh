#! /bin/sh

curl -k https://repository.mostserene.eu/avogador/artifacts/apigateway.jar -u ${REPOSITORY_CREDENTIALS} --output apigateway.jar
java -jar ./apigateway.jar