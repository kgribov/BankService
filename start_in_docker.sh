#!/bin/bash

mvn clean package docker:build

docker run -p 8080:8080 -t -d com.kgribov/bankservice
