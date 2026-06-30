#!/bin/bash
# Build (and optionally push) the non-root xnat-web image. Pulls the PUBLISHED
# war so the container ships the same application bytes as the published WAR
# (file logging swapped to console) -- no source build here. CI-agnostic: call it
# from a Bitbucket Pipeline, Jenkins, GitHub Actions, or locally.
set -euo pipefail

usage() {
    cat << 'EOF'
Usage: build.sh --image <registry/repo:tag> --war-url <JFrog WAR URL> [--war-sha256 <sha>] [--tomcat-image <img>] [--push]

Wraps the published WAR on a Tomcat base as a non-root image. The registry is NOT
hardcoded -- pass the full --image ref so the publish target is the caller's choice.
EOF
}

IMAGE=""
WAR_URL=""
WAR_SHA256=""
TOMCAT_IMAGE="tomcat:9.0-jdk8-temurin-noble"   # JDK8 = 1.9.x; pass jdk21 for 1.10
PUSH=false

while [[ $# -gt 0 ]]; do
    case "$1" in
        --image)        IMAGE="${2:?--image needs a value}"; shift 2 ;;
        --war-url)      WAR_URL="${2:?--war-url needs a value}"; shift 2 ;;
        --war-sha256)   WAR_SHA256="${2:?--war-sha256 needs a value}"; shift 2 ;;
        --tomcat-image) TOMCAT_IMAGE="${2:?--tomcat-image needs a value}"; shift 2 ;;
        --push)         PUSH=true; shift ;;
        -h|--help)      usage; exit 0 ;;
        *) echo "Unknown arg: $1" >&2; usage >&2; exit 1 ;;
    esac
done

[[ -n "$IMAGE" && -n "$WAR_URL" ]] || { usage >&2; exit 1; }

cd "$(dirname "$0")"
echo "Building $IMAGE  (war: $WAR_URL, base: $TOMCAT_IMAGE)"
docker build \
    --build-arg XNAT_WAR_URL="$WAR_URL" \
    --build-arg XNAT_WAR_SHA256="$WAR_SHA256" \
    --build-arg TOMCAT_IMAGE="$TOMCAT_IMAGE" \
    -t "$IMAGE" .

if $PUSH; then
    docker push "$IMAGE"
else
    echo "Built $IMAGE (pass --push to publish)"
fi
