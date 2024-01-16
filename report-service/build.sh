#!/bin/bash
set -e
mvn clean package
docker-compose build report-service
