#!/bin/bash
## need disable niffler e2e test depends
source ./docker.properties
export COMPOSE_PROFILES=allure
export PREFIX="${IMAGE_PREFIX}"

export ALLURE_DOCKER_API=http://allure:5050/
export HEAD_COMMIT_MESSAGE="local build"
export ARCH=$(uname -m)

docker compose down

docker compose up -d
docker ps -a
