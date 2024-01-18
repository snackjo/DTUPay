#!/bin/bash
set -e

# Account service tests
pushd account-service
mvn test
popd

# Payment service tests
pushd payment-service
mvn test
popd

# Report service tests
pushd report-service
mvn test
popd

# Token service tests
pushd token-service
mvn test
popd

# DTU Pay service tests
pushd dtu-pay-service
mvn test
popd