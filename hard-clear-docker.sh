docker stop $(docker ps -a -q) && docker rm $(docker ps -a -q)
docker volume prune -af
docker volume create pgdata
docker volume create allure-results