#!/bin/bash
set -e

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

pushd report-service
chmod +x build.sh
./build.sh
popd

# Update the set of services and
# build and execute the system tests
pushd end-to-end-tests
chmod +x deploy_fast.sh
./deploy_fast.sh
sleep 5
chmod +x test.sh
./test.sh
popd

# Cleanup the build images
docker image prune -f

