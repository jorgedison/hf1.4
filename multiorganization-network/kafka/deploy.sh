docker stack deploy -c orderer-zookeeper.yaml zookeeper

docker stack deploy -c orderer-kafka.yaml kafka

docker stack deploy -c orderer.yaml orderer
