#!/bin/bash

# Build Native Image Script for JManus
# This script builds a GraalVM native image for the JManus application

set -e

echo "Starting GraalVM Native Image build for JManus..."

# Set JAVA_HOME to GraalVM
export JAVA_HOME="/Library/Java/JavaVirtualMachines/graalvm-jdk-21.0.3+7.1/Contents/Home"
export GRAALVM_HOME="/Library/Java/JavaVirtualMachines/graalvm-jdk-21.0.3+7.1/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

echo "Using Java: $(java -version)"
echo "Using Maven: $(mvn -version | head -1)"

# Check if native-image is installed
if ! command -v native-image &> /dev/null; then
    echo "Installing GraalVM native-image..."
    gu install native-image
fi

echo "Clean and compile the project..."
mvn clean compile

echo "Process AOT and build native image..."
mvn -Pnative spring-boot:process-aot native:compile

echo "Native image build completed!"
echo "The native executable should be in target/ directory"

# List the generated files
echo "Generated files:"
ls -la target/ | grep -E "(jmanus|spring-ai-alibaba-jmanus)" | grep -v ".jar"
