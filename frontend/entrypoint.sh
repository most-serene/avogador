#! /bin/sh

curl -k https://repository.mostserene.eu/avogador/artifacts/webapp.tar.gz -u ${REPOSITORY_CREDENTIALS} --output webapp.tar.gz
tar -xzvf webapp.tar.gz -C . 

serve -s dist
