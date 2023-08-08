#! /bin/sh

curl -k https://dev-server.sanve.mostserene.eu/jars/userservice.jar --output userservice.jar
java -jar ./userservice.jar