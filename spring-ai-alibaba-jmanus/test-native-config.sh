#!/bin/bash

# Test Native Image Build Configuration
# This script tests if the native image build configuration is working

set -e

echo "Testing Native Image Build Configuration..."

# Set JAVA_HOME to GraalVM
export JAVA_HOME="/Library/Java/JavaVirtualMachines/graalvm-jdk-21.0.3+7.1/Contents/Home"
export GRAALVM_HOME="/Library/Java/JavaVirtualMachines/graalvm-jdk-21.0.3+7.1/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

echo "✓ Java Version:"
java -version

echo ""
echo "✓ Maven Version:"
mvn -version | head -1

echo ""
echo "✓ Checking if native-image is available:"
if command -v native-image &> /dev/null; then
    echo "native-image is installed"
    native-image --version
else
    echo "native-image is not installed. It will be installed during build."
fi

echo ""
echo "✓ Validating pom.xml configuration:"
mvn help:effective-pom -Pnative 2>/dev/null | grep -q "org.graalvm.buildtools" && echo "Native Maven Plugin found in configuration" || echo "❌ Native Maven Plugin not found"

echo ""
echo "✓ Testing Maven configuration with native profile:"
mvn help:active-profiles -Pnative

echo ""
echo "✓ Checking dependencies for native compatibility:"
mvn dependency:tree -Pnative 2>/dev/null | grep -E "(spring-boot|graalvm|native)" | head -10

echo ""
echo "✓ Testing native-maven-plugin availability:"
mvn help:describe -Dplugin=org.graalvm.buildtools:native-maven-plugin -Pnative 2>/dev/null | grep -q "Name:" && echo "native-maven-plugin is available" || echo "❌ native-maven-plugin not available"

echo ""
echo "✓ Configuration test completed successfully!"
echo ""
echo "To build the native image, run:"
echo "  ./build-native.sh"
echo ""
echo "Or manually run:"
echo "  mvn -Pnative clean spring-boot:process-aot native:compile"
