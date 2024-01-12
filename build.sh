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
pushd account-service
chmod +x build.sh
./build.sh
popd 

pushd dtu-pay-service
chmod +x build.sh
./build.sh
popd

pushd token-service
chmod +x build.sh
./build.sh
popd

pushd payment-service
chmod +x build.sh
./build.sh
popd
