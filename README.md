# Spring Cloud Services with Azure SQL

This demo is a simple microservices application, using an AzureSQL or in-memory database. 
It showcases service discovery via Eureka, real-time properties updates via the Spring Cloud Config Server,
client-side load balancing using Zuul, and circuit breakers via Hystrix.

The Frontend is built using Thymeleaf, with a responsive HTML5 template courtesy of 
html5up.net.

## Pre-requisites

You'll need maven, JAVA (including the JRE), and the CF cli. If you want
to test out the OMS integration, you'll also need ruby (at least 2.3.3) 
and the UAAC cli.

## To configure

Set an environment variable for your CF API endpoint:

```bash
export CFAPI=api.system.13.91.6.94.cf.pcfazure.com
```

Set up a config file for your AzureDB rules:
```bash
cp sqldb-example-config.json.example sqldb-example-config.json
sed -i '' 's/CHANGEME/my-secret-password/' sqldb-example-config.json
```

Set up a config file for your Spring Cloud Config Server backend:
```bash
cp config-server-setup.json.example config-server-setup.json
sed -i '' 's/joshuamckenty/mygithubname/' config-server-setup.json
```

Now login to CF, and create the needed services:
```bash
cf login -a http://$CFAPI
./setup-services.sh
```

You'll need to wait until your services are up before trying to run the
application; periodically check `cf services` output for this.

## To secure

If you're not using a PCF environment with real SSL certificates,
your applications will have trouble connecting to Eureka. Fix this:

```bash
cf set-env cover-server TRUST_CERTS $CFAPI
cf set-env cover-client TRUST_CERTS $CFAPI
cf set-env frontend TRUST_CERTS $CFAPI
```

## To deploy

```bash
./deploy.sh
```


### Contributions Welcome!