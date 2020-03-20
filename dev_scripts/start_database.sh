#!/bin/bash

cd ..
./init_env.sh

screen -S db -d -m sudo docker-compose run -p 3306:3306 db