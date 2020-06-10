#!/bin/bash

clear

docker swarm leave -f

docker swarm init 
