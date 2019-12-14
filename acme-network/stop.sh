echo 'Detiene contenedores'
sudo docker stop $(docker ps -aq)

echo 'Elimina contenedores'
sleep 2
sudo docker rm -f $(docker ps -aq)

echo 'Elimina redes'
sleep 2
sudo docker network prune --force

echo 'Elimina volumenes'
sleep 2
sudo docker volume prune --force

echo 'Lista de contenedores'
sleep 2
sudo docker ps -a
