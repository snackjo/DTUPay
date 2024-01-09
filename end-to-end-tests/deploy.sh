#!/bin/bash
set -e
docker image prune -f
docker-compose up -d rabbitMq
sleep 20
docker-compose up -d student-registration-service student-id-service 

