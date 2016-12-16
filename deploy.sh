#!/usr/bin/env bash
mvn -DskipTests clean package
cf push
# cf set-env cover-server TRUST_CERTS api.system.13.91.6.94.cf.pcfazure.com
# curl -d {} http://cover-client.app.13.91.6.94.cf.pcfazure.com/refresh
