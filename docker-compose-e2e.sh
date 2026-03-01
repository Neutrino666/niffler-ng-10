#!/bin/bash
source ./docker.properties
export COMPOSE_PROFILES=test
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"

export ALLURE_DOCKER_API=http://allure:5050/
export HEAD_COMMIT_MESSAGE="local build"
export ARCH=$(uname -m)

docker compose down
docker_containers=$(docker ps -a -q)
docker_images=$(docker images --format '{{.Repository}}:{{.Tag}}' | grep 'niffler')
fast=false;
browser=chrome
gh_token=$GITHUB_TOKEN
if ! -z  [ "$gh_token" ]; then
   echo "Для тестов необходима переменная OS: GITHUB_TOKEN"
   exit 1
fi
export GITHUB_TOKEN=$gh_token

usage() {
  cat <<EOF
Скрипт поднятия окружения и прогона тестов в docker compose.
Для прогона тестов необходима переменная OS: GITHUB_TOKEN

Параметры:
  -f         fast - режим переиспользования images
  -b ARG     browser - браузер на котором будут запущены тесты 'chrome/firefox'
             default: chrome
  -h         help

Примеры:
${0##*/} [-f] [-b] [ARG]
${0##*/} [-fb] [ARG]
${0##*/} [-b] [ARG]
${0##*/} [-f]
${0##*/}
EOF
}

while getopts ":fb:h" opt; do
  case $opt in
  b) browser=$OPTARG;;
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

if [ ! -z "$browser" ]; then
    BROWSER=chrome
fi
export BROWSER=$browser

if [ $fast = false ]; then
  if [ ! -z "$docker_images"  ]; then
      echo "### Remove images: $docker_images ###"
        docker rmi $docker_images
  fi
fi

echo '### Run mode ###'
echo "fast: $fast, exist images: $docker_images"

echo '### Java version ###'
java --version
bash ./gradlew clean


bash ./gradlew jibDockerBuild -x :niffler-e-2-e-tests:test -Duser.timezone=UTC

if [ "$browser" = "firefox" ]; then
  echo '### Download firefox image ###'
  docker pull twilio/selenoid:firefox_stable_148
else
  echo '### Download chrome image ###'
  docker pull twilio/selenoid:chrome_stable_140
fi

docker compose up -d
docker ps -a
