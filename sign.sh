#!/bin/bash
# Sign the APK for distribution
# Usage: ./sign.sh

set -e

APK_PATH="app/build/outputs/apk/release/app-release-unsigned.apk"
SIGNED_APK="app/build/outputs/apk/release/app-release-signed.apk"
KEYSTORE="release.keystore"

if [ ! -f "$APK_PATH" ]; then
    echo "Error: Unsigned APK not found. Run ./build.sh first."
    exit 1
fi

if [ ! -f "$KEYSTORE" ]; then
    echo "Creating new keystore..."
    keytool -genkey -v -keystore "$KEYSTORE" -alias clockwidget -keyalg RSA -keysize 2048 -validity 10000
fi

echo "Signing APK..."
apksigner sign --ks "$KEYSTORE" --ks-key-alias clockwidget --out "$SIGNED_APK" "$APK_PATH"

echo ""
echo "✅ Signed APK: $SIGNED_APK"
echo "To install: adb install $SIGNED_APK"
