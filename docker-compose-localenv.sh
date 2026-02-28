#!/bin/bash
source ./docker.properties
export COMPOSE_PROFILES=local
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"

docker compose down
docker_containers=$(docker ps -a -q)
docker_images=$(docker images --format '{{.Repository}}:{{.Tag}}' | grep 'niffler')
fast=false;

usage() {
  cat <<EOF
Скрипт поднятия окружения и прогона тестов в docker compose.

Examples:
${0##*/} [-f] [-b ARG]
${0##*/} [-fb] [ARG]
${0##*/} [-b] [ARG]
${0##*/} [-f]
${0##*/}

Параметры:
  -f         fast - режим переиспользования images
  -h         help
EOF
}

while getopts ":fh" opt; do
  case $opt in
  f) fast=true;;
  h) usage ; exit 0;;
  \?) echo "Неизвестная опция -$OPTARG" >&2; usage ; exit 0 ;;
  esac
done

if [ ! -z "$docker_containers" ]; then
  echo "### Stop containers: $docker_containers ###"
  docker stop $docker_containers
  docker rm $docker_containers
fi

if [ $fast = false ]; then
  if [ ! -z "$docker_images"  ]; then
      echo "### Remove images: $docker_images ###"
        docker rmi $docker_images
  fi
fi

echo '### Run mode ###'
echo "fast: $fast, exist images: $docker_images"

docker compose up -d
docker ps -a