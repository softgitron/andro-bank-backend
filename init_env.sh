#!/bin/bash

# THESE VALUES SHOULD BE UPDATED BY YOU
# Values will be passed to docker containers
MYSQL_PASSWORD="YOUR PASSWORD HERE"
MYSQL_ROOT_PASSWORD="YOUR PASSWORD HERE"

export MYSQL_DATABASE
export MYSQL_USER
export MYSQL_PASSWORD
export MYSQL_ROOT_PASSWORD

echo $MYSQL_PASSWORD > ./db_password.txt
echo $MYSQL_ROOT_PASSWORD > ./db_root_password.txt

sleep 1
