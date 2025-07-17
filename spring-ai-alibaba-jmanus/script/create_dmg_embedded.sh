#!/bin/bash

# DMG creation script for JManus with embedded JDK on macOS
# This script creates a distributable DMG package with embedded JDK

set -e

# Configuration
APP_NAME="JManus"
APP_BUNDLE_NAME="JManus.app"
DMG_NAME="JManus-Installer"
TEMP_DMG_NAME="temp-${DMG_NAME}"
VOLUME_NAME="JManus Installer"

# Parse arguments
PLATFORM=""
VERSION="3.0.0-SNAPSHOT"

while [[ $# -gt 0 ]]; do
    case $1 in
        --platform)
            PLATFORM="$2"
            shift 2
            ;;
        --version)
            VERSION="$2"
            shift 2
            ;;
        *)
            echo "Unknown option: $1"
            exit 1
            ;;
    esac
done

if [ -z "$PLATFORM" ]; then
    echo "Usage: $0 --platform <platform> [--version <version>]"
    echo "Example: $0 --platform macos-aarch64 --version 3.0.0"
    exit 1
fi

echo "Creating DMG for platform: $PLATFORM"
echo "Version: $VERSION"

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
DIST_DIR="$PROJECT_ROOT/dist/$PLATFORM"

# Check if distribution exists
if [ ! -d "$DIST_DIR" ]; then
    echo "Error: Distribution directory not found at $DIST_DIR"
    echo "Please run build_embedded_jdk.sh first"
    exit 1
fi

# Create temporary directory structure
TEMP_DIR=$(mktemp -d)
APP_BUNDLE_DIR="$TEMP_DIR/$APP_BUNDLE_NAME"
APP_CONTENTS_DIR="$APP_BUNDLE_DIR/Contents"
APP_MACOS_DIR="$APP_CONTENTS_DIR/MacOS"
APP_RESOURCES_DIR="$APP_CONTENTS_DIR/Resources"

mkdir -p "$APP_MACOS_DIR"
mkdir -p "$APP_RESOURCES_DIR"

# Copy the entire distribution to the app bundle
cp -r "$DIST_DIR"/* "$APP_RESOURCES_DIR/"

# Create main executable wrapper script
cat > "$APP_MACOS_DIR/JManus" << 'EOF'
#!/bin/bash
# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_RESOURCES="$SCRIPT_DIR/../Resources"

# Change to resources directory
cd "$APP_RESOURCES"

# Export API key if set
if [ -n "$DASHSCOPE_API_KEY" ]; then
    export DASHSCOPE_API_KEY
fi

# Launch the application using the embedded startup script
exec "$APP_RESOURCES/bin/start_jmanus.sh" --terminal
EOF

chmod +x "$APP_MACOS_DIR/JManus"

# Create Info.plist for the app bundle
cat > "$APP_CONTENTS_DIR/Info.plist" << EOF
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>CFBundleExecutable</key>
    <string>JManus</string>
    <key>CFBundleIdentifier</key>
    <string>com.alibaba.spring-ai.jmanus</string>
    <key>CFBundleName</key>
    <string>JManus</string>
    <key>CFBundleDisplayName</key>
    <string>JManus</string>
    <key>CFBundleVersion</key>
    <string>$VERSION</string>
    <key>CFBundleShortVersionString</key>
    <string>$VERSION</string>
    <key>CFBundlePackageType</key>
    <string>APPL</string>
    <key>CFBundleInfoDictionaryVersion</key>
    <string>6.0</string>
    <key>LSMinimumSystemVersion</key>
    <string>10.15</string>
    <key>LSApplicationCategoryType</key>
    <string>public.app-category.developer-tools</string>
    <key>NSHighResolutionCapable</key>
    <true/>
    <key>NSRequiresAquaSystemAppearance</key>
    <false/>
    <key>LSEnvironment</key>
    <dict>
        <key>LC_CTYPE</key>
        <string>UTF-8</string>
    </dict>
</dict>
</plist>
EOF

# Create PkgInfo
echo "APPLNONE" > "$APP_CONTENTS_DIR/PkgInfo"

# Create the DMG staging area
DMG_STAGING_DIR="$TEMP_DIR/dmg"
mkdir -p "$DMG_STAGING_DIR"

# Copy app bundle to staging area
cp -r "$APP_BUNDLE_DIR" "$DMG_STAGING_DIR/"

# Create a symlink to Applications folder
ln -s /Applications "$DMG_STAGING_DIR/Applications"

# Add additional files
mkdir -p "$DMG_STAGING_DIR/Documentation"
cp "$DIST_DIR/docs"/*.md "$DMG_STAGING_DIR/Documentation/" 2>/dev/null || true
cp "$DIST_DIR/BUILD_INFO.txt" "$DMG_STAGING_DIR/" 2>/dev/null || true

# Create installation instructions
cat > "$DMG_STAGING_DIR/INSTALL.txt" << EOF
JManus Installation Instructions
================================

1. Drag the JManus.app to the Applications folder
2. Open JManus from Applications or Launchpad
3. When prompted, enter your DashScope API Key
4. The application will start and be available at http://localhost:18080

System Requirements:
- macOS 10.15 or later
- No additional Java installation required (JDK 21 is embedded)

For more information, see the Documentation folder.

Version: $VERSION
Platform: $PLATFORM
Build Date: $(date)
EOF

# Calculate DMG size (add 50MB buffer)
SIZE_MB=$(du -sm "$DMG_STAGING_DIR" | cut -f1)
SIZE_MB=$((SIZE_MB + 50))

# Create the DMG
DMG_PATH="$PROJECT_ROOT/dist/${DMG_NAME}-${VERSION}-${PLATFORM}.dmg"
TEMP_DMG_PATH="$PROJECT_ROOT/dist/${TEMP_DMG_NAME}.dmg"

echo "Creating DMG (${SIZE_MB}MB)..."

# Remove existing DMG files
rm -f "$DMG_PATH" "$TEMP_DMG_PATH"

# Ensure no previous DMG mounts are left
if [ -n "$(hdiutil info | grep -E "JManus.*Installer")" ]; then
    echo "Unmounting any existing JManus DMG mounts..."
    hdiutil info | grep -E "JManus.*Installer" | awk '{print $1}' | xargs -I {} hdiutil detach {} 2>/dev/null || true
fi

# Wait a moment for system to clean up
sleep 2

# Create temporary DMG
hdiutil create -srcfolder "$DMG_STAGING_DIR" \
    -volname "$VOLUME_NAME" \
    -fs HFS+ \
    -fsargs "-c c=64,a=16,e=16" \
    -format UDRW \
    -size "${SIZE_MB}m" \
    "$TEMP_DMG_PATH"

# Mount the temporary DMG
MOUNT_POINT=$(hdiutil attach -readwrite -noverify -noautoopen "$TEMP_DMG_PATH" | grep -E '^/dev/' | sed 1q | awk '{print $3}')

# Set DMG window properties and background
if [ -n "$MOUNT_POINT" ]; then
    echo "Configuring DMG appearance..."
    
    # Create .DS_Store for window settings
    echo '
    on run argv
        tell application "Finder"
            tell disk "'$VOLUME_NAME'"
                open
                set current view of container window to icon view
                set toolbar visible of container window to false
                set statusbar visible of container window to false
                set the bounds of container window to {100, 100, 600, 400}
                set viewOptions to the icon view options of container window
                set arrangement of viewOptions to not arranged
                set icon size of viewOptions to 72
                set position of item "JManus.app" of container window to {150, 120}
                set position of item "Applications" of container window to {350, 120}
                if exists item "INSTALL.txt" then
                    set position of item "INSTALL.txt" of container window to {250, 220}
                end if
                if exists item "Documentation" then
                    set position of item "Documentation" of container window to {400, 220}
                end if
                update without registering applications
                delay 2
            end tell
        end tell
    end run
    ' | osascript
    
    # Give the system time to write the .DS_Store file
    sleep 3
    
    # Unmount
    hdiutil detach "$MOUNT_POINT"
fi

# Convert to compressed read-only DMG
echo "Compressing DMG..."

# Add retry logic for hdiutil convert
for attempt in 1 2 3; do
    if hdiutil convert "$TEMP_DMG_PATH" \
        -format UDZO \
        -imagekey zlib-level=9 \
        -o "$DMG_PATH"; then
        echo "DMG compression successful on attempt $attempt"
        break
    else
        echo "DMG compression failed on attempt $attempt"
        if [ $attempt -eq 3 ]; then
            echo "Failed to compress DMG after 3 attempts"
            exit 1
        fi
        echo "Waiting 5 seconds before retry..."
        sleep 5
    fi
done

# Clean up
rm -f "$TEMP_DMG_PATH"
rm -rf "$TEMP_DIR"

# Verify the DMG
if [ -f "$DMG_PATH" ]; then
    DMG_SIZE=$(ls -lh "$DMG_PATH" | awk '{print $5}')
    echo "DMG created successfully: $DMG_PATH ($DMG_SIZE)"
    
    # Test mount the DMG to verify it works
    echo "Verifying DMG..."
    TEST_MOUNT=$(hdiutil attach -readonly -noverify -noautoopen "$DMG_PATH" | grep -E '^/dev/' | sed 1q | awk '{print $3}')
    if [ -n "$TEST_MOUNT" ]; then
        echo "DMG verification successful"
        hdiutil detach "$TEST_MOUNT" >/dev/null
    else
        echo "Warning: DMG verification failed"
    fi
else
    echo "Error: Failed to create DMG"
    exit 1
fi

echo "DMG creation completed: $DMG_PATH"
