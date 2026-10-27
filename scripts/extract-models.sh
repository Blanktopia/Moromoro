#!/usr/bin/env bash

set -e

if [ $# -eq 0 ]; then
    echo "Usage: $0 <minecraft.jar>"
    echo "Example: $0 ~/.minecraft/versions/26.3/26.3.jar"
    exit 1
fi

JAR_FILE="$1"
ITEMS_DIR="src/main/resources/default/items"

if [ ! -f "$JAR_FILE" ]; then
    echo "Error: File '$JAR_FILE' not found"
    exit 1
fi

echo "Extracting items from $JAR_FILE..."

rm -rf "$ITEMS_DIR"
mkdir -p "$ITEMS_DIR"

unzip -q -o "$JAR_FILE" "assets/minecraft/items/*" -d /tmp/moromoro-extract
cp -r /tmp/moromoro-extract/assets/minecraft/items/* "$ITEMS_DIR/"
rm -rf /tmp/moromoro-extract

echo "Successfully extracted items to $ITEMS_DIR"
