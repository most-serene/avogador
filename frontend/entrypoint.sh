#! /bin/sh

curl -k https://dev-server.sanve.mostserene.eu/jars/webapp.tar.gz -u ${REPOSITORY_CREDENTIALS} --output webapp.tar.gz
tar -xzvf webapp.tar.gz -C . 

serve -s dist
