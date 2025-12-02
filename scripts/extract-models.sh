#!/usr/bin/env bash

set -e

if [ $# -eq 0 ]; then
    echo "Usage: $0 <minecraft.jar>"
    echo "Example: $0 ~/.minecraft/versions/1.21.5/1.21.5.jar"
    exit 1
fi

JAR_FILE="$1"
MODELS_DIR="src/main/resources/default/models"
ITEMS_DIR="src/main/resources/default/items"

if [ ! -f "$JAR_FILE" ]; then
    echo "Error: File '$JAR_FILE' not found"
    exit 1
fi

echo "Extracting models and items from $JAR_FILE..."

# Create target directories if they don't exist
mkdir -p "$MODELS_DIR"
mkdir -p "$ITEMS_DIR"

# Extract assets/minecraft/models/ and assets/minecraft/items/ from the jar
unzip -q -o "$JAR_FILE" "assets/minecraft/models/*" "assets/minecraft/items/*" -d /tmp/moromoro-extract

# Move extracted files to target directories
cp -r /tmp/moromoro-extract/assets/minecraft/models/* "$MODELS_DIR/"
cp -r /tmp/moromoro-extract/assets/minecraft/items/* "$ITEMS_DIR/"

# Clean up
rm -rf /tmp/moromoro-extract

echo "Successfully extracted models to $MODELS_DIR"
echo "Successfully extracted items to $ITEMS_DIR"
