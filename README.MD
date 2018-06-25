[![Build Status](https://travis-ci.org/kgribov/BankService.svg?branch=master)](https://travis-ci.org/kgribov/BankService)

# BankService

A small REST service for creating accounts and transfers between them.

## How to run

### Java
Firstly build project using maven:
```
mvn clean package
```

Run service with specific port (f.e. 8080):
```
java -jar target/bank_service.jar 8080
```

Or you could use script: *start_locally.sh*
### Docker
It is possible to run service as a **Docker** container.

Build project:
```
mvn clean package docker:build
```

Run as a container:
```
docker run -p 8080:8080 -t -d com.kgribov/bankservice
```

Or you could use script: *start_in_docker.sh*

## Documentation
Documentation was created with [Swagger](https://swagger.io/),
visit website to read about all service endpoints and try them with **Swagger** UI: http://bankservice.kgribov.me/doc

## Try it
You could try service deployed on http://bankservice.kgribov.me/stats
