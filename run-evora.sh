#!/bin/bash

# Evora Environment Initializer
# This script starts the Dockerized dependencies and prepares the framework.

echo "--- Initializing Évora Environment (Docker) ---"

# Step 1: Start dependencies
if ! command -v docker-compose &> /dev/null
then
    echo "ERROR: docker-compose not found. Please install Docker Compose first."
    exit 1
fi

docker-compose up -d

# Step 2: Wait for services to be healthy
echo "Waiting for ActiveMQ (AMQP) to be ready on port 61616..."
while ! nc -z localhost 61616; do   
  printf "."
  sleep 2
done
echo -e "\nActiveMQ is UP."

echo "Waiting for Axis2 to be ready on port 8080..."
while ! nc -z localhost 8080; do   
  printf "."
  sleep 2
done
echo -e "\nAxis2 is UP."

# Step 3: Build the Evora project
echo "--- Compiling Évora Research Framework ---"
mvn clean compile

echo "--- Setup Complete ---"
echo "You can now run the sample orchestration using:"
echo "java -cp target/classes org.evora.core.EvoraMain"
echo "---"
