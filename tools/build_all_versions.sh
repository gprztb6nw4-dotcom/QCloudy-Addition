#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd "$(dirname "$0")/.." && pwd)"
cd "$project_dir"

build_target() {
    local minecraft_version="$1"
    local build_suffix="$2"
    ./gradlew --no-daemon clean test build prepareRelease \
        -Pminecraft_version="$minecraft_version" \
        -Ptarget_build_dir="build/$build_suffix"
}

build_target "26.1.2" "26.1.2"
build_target "26.2" "26.2"

echo "Built both supported Minecraft versions in release/."
