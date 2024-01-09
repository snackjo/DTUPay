#!/bin/bash
set -e

# Build and install the libraries
# abstracting away from using the
# RabbitMq message queue
pushd messaging-utilities
chmod +x build.sh
./build.sh
popd 

# Build the services
pushd student-id-service
chmod +x build.sh
./build.sh
popd 

pushd student-registration-service
chmod +x build.sh
./build.sh
popd 
