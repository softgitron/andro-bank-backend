# Andro-bank-backend

Backend service written in java related to object-oriented programming course <br>
Server currently deployed at: [https://qlist.ddns.net/](https://qlist.ddns.net/)

## About this project

This project was made in conjunction with Andro-bank-frontend application that can be found from [andro-bank](https://github.com/softgitron/andro-bank) repository. This project tries to emulate inner workings of the real-world banking applications. No real money is handeled inside this application.

## Quick start

Server is fairly easy to setup since its fully Docker compatible.
Steps:

1. Install docker and docker compose
   - View these sites to get instruction how to install required packages [docker](https://www.docker.com/) / [docker compose](https://docs.docker.com/compose/)
   - On my personal favorite Linux distribution Arch-linux packages may be installed with command `sudo pacman -S docker docker-compose`
2. Clone this repository with command `git clone https://github.com/softgitron/andro-bank-backend.git`
3. Configure cloned repository
   - Open _init_env.sh_ file with your Favorite text editor
   - Change _MYSQL_PASSWORD_ and _MYSQL_ROOT_PASSWORD_ to your liking.
   - _MYSQL_PASSWORD_ and _MYSQL_ROOT_PASSWORD_ should contain long random strings for maximum security. Special characters in mysql passwords may cause problems so it is not recommended.
   - It's **IMPORTANT** to set these values since without them security would be severely impacted.
4. Build containers
   - Build containers using command `./prepare.sh`
5. Start server
   - Start server with command `./start.sh`
6. After some waiting server should be up and running. Server can be accessed by default from address http://localhost:8080

## Documentation

ApiDoc documentation is available [here](https://softgitron.github.io/andro-bank-backend/apidoc/index.html)

## Manual building

Backend can be manually build using gradle files provided in the repo. Run `gradle build` command in order to build backend manually. Output jar will be available at _./server/build/libs_

## Development stuff

### Unit tests

Server API can be unit tested using custom made test framework that is available in _test_-directory. Tests can be run using command `./run_tests.py`. All tests are currently listed in the _tests.py_-file. There is lots of good examples how to use API inside _tests.py_-file. It is good idea to take a look inside the file during development of the client for this API.

### Dev scripts

_Dev_scripts_-folder contains some handy scripts for testing and development. _clean_docker_build.sh_-script resets docker compleatly and rebuilds server. Note this script completely destroys **ALL CONTAINERS MACHINE WIDE** so proceed with caution before running this script. _curl.sh_-script contains various curl commands that can be used to test API-calls. _start_database.sh_ and _stop_database.sh_ scripts can be used for starting and stopping mysql database independently for development purposes.
