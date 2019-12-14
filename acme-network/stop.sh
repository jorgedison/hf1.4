sudo docker stop $(docker ps -aq)

sudo docker rm -f $(docker ps -aq)

sudo docker network prune --force

sudo docker volume prune --force
