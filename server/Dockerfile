FROM archlinux:latest
RUN pacman --noconfirm -Syu
RUN pacman --noconfirm -S jdk-openjdk gradle

# Create app directory
WORKDIR /usr/src/app
COPY . .

RUN export JAVA_HOME=/usr/lib/jvm/default && gradle build

RUN pacman --noconfirm -R gradle

CMD ["java", "-jar", "./build/libs/Server.jar"]