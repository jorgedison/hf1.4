export COMPOSE_PROJECT_NAME=acmenetwork
export CHANNEL_NAME=marketplace
export VERBOSE=false
export FABRIC_CFG_PATH=$PWD

CHANNEL_NAME=$CHANNEL_NAME docker-compose -f docker-compose.yml  up -d

docker ps
