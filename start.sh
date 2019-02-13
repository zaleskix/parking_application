#!/usr/bin/env bash

mvn clean install -DskipTests
mvn spring-boot:run

echo "Click any key to exit "
read end