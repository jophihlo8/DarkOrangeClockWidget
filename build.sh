#!/bin/bash
# Build the APK
# Usage: ./build.sh

set -e

echo "Building Dark Orange Clock Widget..."

if ! command -v ./gradlew &> /dev/null; then
    echo "Error: gradlew not found. Make sure you're in the project root."
    exit 1
fi

./gradlew assembleRelease

echo ""
echo "✅ Build complete!"
echo "APK location: app/build/outputs/apk/release/app-release-unsigned.apk"
echo ""
echo "To install on a connected device:"
echo "  adb install app/build/outputs/apk/release/app-release-unsigned.apk"
echo ""
echo "To sign the APK for distribution, run:"
echo "  ./sign.sh"
