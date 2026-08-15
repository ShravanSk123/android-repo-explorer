#!/usr/bin/env bash
set -euo pipefail

ACTION="${1:-}"

case "$ACTION" in
  test)
    echo "Running unit tests and lint..."
    ./gradlew test lintDebug --no-daemon
    ;;
  assemble)
    echo "Building debug APK..."
    ./gradlew assembleDebug --no-daemon
    ;;
  *)
    echo "Usage: $0 {test|assemble}" >&2
    exit 1
    ;;
esac

echo "Done: $ACTION"