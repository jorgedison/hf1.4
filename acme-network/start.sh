echo "Seteo de variables"
export COMPOSE_PROJECT_NAME=acmenetwork
export CHANNEL_NAME=marketplace
export VERBOSE=false
export FABRIC_CFG_PATH=$PWD

echo "Inicia Red Blockchain"
sleep 2
CHANNEL_NAME=$CHANNEL_NAME docker-compose -f docker-compose.yml  up -d

echo "Lista de contenedores"
sleep 2
docker ps
