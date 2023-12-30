# noob

A Discord bot to empower the noob.

## Setup

    # Java
    brew install openjdk@21

    # Clojure
    brew install clojure

## Config

Create `.env` in the same directory as `deps.edn` with the following 
structure and add your settings:

````clojure
DISCORD_TOKEN=<SECRET>
DISCORD_APP_ID=<SECRET>
DISCORD_DEV_GUILD=<SECRET>
````

## Database

### Download

    # Download and unzip datomic-pro 1.0.6735
    curl https://datomic-pro-downloads.s3.amazonaws.com/1.0.6735/datomic-pro-1.0.6735.zip -O
    unzip datomic-pro-1.0.6735.zip
    rm datomic-pro-1.0.6735.zip

### Start Datomic

    java -v
    # make sure you're running Java 21
    bin/db

### Schema / Migration

    # migrate to ensure database schema matches code
    clj -Mmigrate:test

## Running Server Locally

Make sure datomic is running...

    clj -Mtest:run

## Running Tests

    # clojure specs:
    clj -Mspec:test

    # clojure specs automatically running when fileds are changed:
    clj -Mspec:test -a

## Deployment

Use the infrastructure repo

### Create EC2 Instance

    Ubuntu 22.04 t2.small works fine

### Update

    # Update apt
    sudo apt update
    sudo apt upgrade

### Install

#### Sqlite3

    # Good for debugging
    sudo apt install sqlite3

#### Java 21

    # Prerequisites
    sudo apt install wget

    # Install
    wget https://download.oracle.com/java/21/archive/jdk-21.0.1_linux-x64_bin.tar.gz
    sudo tar -xzvf jdk-21.0.1_linux-x64_bin.tar.gz -C /usr/local/

    # Cleanup
    rm jdk-21.0.1_linux-x64_bin.tar.gz
    
    # Environment
    echo 'export JAVA_HOME=/usr/local/jdk-21.0.1' >> ~/.bashrc
    echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
    source ~/.bashrc


#### Clojure

    # Prerequisites
    sudo apt install bash curl rlwrap

    # Install
    curl -L -O https://github.com/clojure/brew-install/releases/latest/download/linux-install.sh
    chmod +x linux-install.sh
    sudo ./linux-install.sh
    
    # Cleanup
    rm ./linux-install.sh

#### SSH KeyGen (optional)

    ssh-keygen -t ed25519 -C "you@example.com"
    eval "$(ssh-agent -s)"

    # Add this outpput to your GitHub account
    cat ~/.ssh/id_ed25519.pub

#### App

    # Clone
    git clone https://github.com/brandoncorrea/nooblj.git
    cd nooblj

    # env settings
    echo "DISCORD_TOKEN=SECRET_TOKEN" >> .env
    echo "DISCORD_APP_ID=APP_ID" >> .env
    echo "DISCORD_DEV_GUILD=GUILD_ID" >> .env
    echo "ENVIRONMENT=prod" >> .env

    # initialize db and log
    mkdir db
    touch log.txt

#### Migrate

    clj -Mtest:migrate sync
    clj -Mtest:migrate

#### Service

Write the following to `/etc/systemd/system/noob.service`

```
[Unit]
Description=Noob Service
StartLimitIntervalSec=30
StartLimitBurst=5

[Service]
Type=simple
Restart=always
RestartSec=1
Environment=JAVA_HOME=/usr/local/jdk-21.0.1
WorkingDirectory=/home/ubuntu/nooblj
ExecStart=/bin/bash -ce "exec /usr/local/bin/clj -M:run >> /home/ubuntu/nooblj/log.txt 2>&1"
User=ubuntu

[Install]
WantedBy=multi-user.target
```

#### Run

    sudo systemctl daemon-reload
    sudo systemctl start noob
