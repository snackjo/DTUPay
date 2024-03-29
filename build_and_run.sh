#!/bin/bash
set -e

chmod +x build.sh
./build.sh


# Update the set of services and
# build and execute the system tests
pushd end-to-end-tests
chmod +x deploy.sh
./deploy.sh 
sleep 5
chmod +x test.sh
./test.sh
popd

# Cleanup the build images
docker image prune -f

