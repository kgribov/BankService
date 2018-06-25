#!/bin/bash

mvn clean package
java -jar target/bank_service.jar $1
