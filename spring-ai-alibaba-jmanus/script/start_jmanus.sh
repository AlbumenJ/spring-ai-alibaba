#!/bin/bash

# JManus macOS Launcher Script
# This script will prompt for DASHSCOPE_API_KEY and start the JManus application

set -e

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Path to the native executable
JMANUS_EXECUTABLE="$SCRIPT_DIR/bin/spring-ai-alibaba-jmanus"

# Check if the executable exists
if [ ! -f "$JMANUS_EXECUTABLE" ]; then
    echo "Error: JManus executable not found at $JMANUS_EXECUTABLE"
    echo "Please make sure the application is properly installed."
    exit 1
fi

# Make sure the executable has proper permissions
chmod +x "$JMANUS_EXECUTABLE"

# Function to prompt for API key
prompt_for_api_key() {
    echo "==========================================="
    echo "       Welcome to JManus Application"
    echo "==========================================="
    echo ""
    echo "To use JManus, you need to provide your DashScope API Key."
    echo "You can get your API key from: https://dashscope.console.aliyun.com/"
    echo ""
    
    # Check if API key is already set in environment
    if [ -n "$DASHSCOPE_API_KEY" ]; then
        echo "Using existing DASHSCOPE_API_KEY from environment."
        return 0
    fi
    
    # Prompt for API key
    while [ -z "$DASHSCOPE_API_KEY" ]; do
        echo -n "Please enter your DashScope API Key: "
        read -s DASHSCOPE_API_KEY
        echo ""
        
        if [ -z "$DASHSCOPE_API_KEY" ]; then
            echo "API Key cannot be empty. Please try again."
            echo ""
        fi
    done
    
    export DASHSCOPE_API_KEY
}

# Function to start JManus
start_jmanus() {
    echo ""
    echo "Starting JManus application..."
    echo "Application will be available at: http://localhost:18080"
    echo ""
    echo "Press Ctrl+C to stop the application."
    echo ""
    
    # Start the application
    "$JMANUS_EXECUTABLE"
}

# Main execution
main() {
    # Detect if running from GUI (double-click) or terminal
    if [ -z "$TERM" ] || [ "$TERM" = "dumb" ]; then
        # Running from GUI (double-click), open a new Terminal window
        osascript -e "tell application \"Terminal\" to do script \"cd '$SCRIPT_DIR' && '$0' --terminal\""
        exit 0
    elif [ "$1" = "--terminal" ]; then
        # Running in Terminal window opened by osascript
        clear
        cd "$SCRIPT_DIR"
        prompt_for_api_key
        start_jmanus
    else
        # Running from existing terminal
        clear
        cd "$SCRIPT_DIR"
        prompt_for_api_key
        start_jmanus
    fi
}

# Handle script termination
trap 'echo ""; echo "JManus application stopped."; exit 0' INT TERM

# Run main function
main "$@"
