docker stack deploy -c orderer.yaml orderer

sleep 1

docker service logs -f orderer_orderer_org1
