#! /bin/sh

curl -k https://dev-server.sanve.mostserene.eu/jars/apigateway.jar --output apigateway.jar
java -jar ./apigateway.jar