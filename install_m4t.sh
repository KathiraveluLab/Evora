#!/bin/bash

# Messaging4Transport Local Installation Script
# This script clones and installs Messaging4Transport to the local Maven repository.

REPO_URL="https://github.com/KathiraveluLab/messaging4transport.git"
TEMP_DIR="m4t_temp_install"

echo "Checking for Maven..."
if ! command -v mvn &> /dev/null
then
    echo "ERROR: Maven (mvn) could not be found."
    echo "Please install it using: sudo apt install maven"
    exit 1
fi

echo "Cloning Messaging4Transport..."
git clone $REPO_URL $TEMP_DIR

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to clone the repository."
    exit 1
fi

cd $TEMP_DIR

echo "Installing to local Maven repository..."
mvn clean install -DskipTests

if [ $? -eq 0 ]; then
    echo "SUCCESS: Messaging4Transport installed successfully."
else
    echo "ERROR: Maven installation failed."
fi

# Cleanup
echo "Cleaning up temporary files..."
cd ..
rm -rf $TEMP_DIR

echo "Done."
