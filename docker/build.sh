#!/bin/bash
# Build (and optionally push) the non-root xnat-web image. Pulls the PUBLISHED
# war so the container ships the same bytes as the metal/file install -- no
# source build here. CI-agnostic: call it from a Bitbucket Pipeline, Jenkins,
# GitHub Actions, or locally.
#
#   ./build.sh --image <registry/repo:tag> --war-url <JFrog WAR URL> [--tomcat-image <img>] [--push]
#
# The registry is NOT hardcoded -- pass the full --image ref so the publish
# target is the caller's decision.
set -euo pipefail

IMAGE=""
WAR_URL=""
TOMCAT_IMAGE="tomcat:9.0-jdk8-temurin-noble"   # JDK8 = 1.9.x; pass jdk21 for 1.10
PUSH=false

while [[ $# -gt 0 ]]; do
    case "$1" in
        --image)        IMAGE="$2"; shift 2 ;;
        --war-url)      WAR_URL="$2"; shift 2 ;;
        --tomcat-image) TOMCAT_IMAGE="$2"; shift 2 ;;
        --push)         PUSH=true; shift ;;
        -h|--help)      sed -n '2,12p' "$0"; exit 0 ;;
        *) echo "Unknown arg: $1" >&2; exit 1 ;;
    esac
done

[[ -n "$IMAGE" && -n "$WAR_URL" ]] || {
    echo "Usage: $0 --image <registry/repo:tag> --war-url <url> [--tomcat-image <img>] [--push]" >&2
    exit 1
}

cd "$(dirname "$0")"
echo "Building $IMAGE  (war: $WAR_URL, base: $TOMCAT_IMAGE)"
docker build \
    --build-arg XNAT_WAR_URL="$WAR_URL" \
    --build-arg TOMCAT_IMAGE="$TOMCAT_IMAGE" \
    -t "$IMAGE" .

if $PUSH; then
    docker push "$IMAGE"
else
    echo "Built $IMAGE (pass --push to publish)"
fi
