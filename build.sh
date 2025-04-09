#!/bin/bash
set -e

echo "=== Building Full-Stack Application Package ==="

# Navigate to project root directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

echo "=== Building Angular Frontend ==="
cd frontend
npm install
npm run build

echo "=== Preparing Backend Resources ==="
mkdir -p ../backend/src/main/resources/webroot
cp -r dist/angular-vertx-app/* ../backend/src/main/resources/webroot/

echo "=== Building Backend Fat JAR ==="
cd ../backend
./gradlew clean shadowJar

echo "=== Build Complete ==="
echo "Executable JAR created at: $(pwd)/build/libs/vertx-api-1.0-SNAPSHOT-fat.jar"
echo ""
echo "Run the application with:"
echo "java -jar $(pwd)/build/libs/vertx-api-1.0-SNAPSHOT-fat.jar" 