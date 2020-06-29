docker network create --driver overlay --subnet=10.200.1.0/24 --attachable my-net

sleep 2

docker network ls | grep "my-net"
