# noob

A Discord bot to empower the noob.

## Setup

    # Java
    brew install openjdk@17

    # Clojure
    brew install clojure

## Config

Create `config.edn` in the same directory as `project.clj` with the following 
structure and add your Discord bot's token:

````clojure
{:token "BOT_TOKEN"}
````

## Database

### Download

    # Download and unzip datomic-pro 1.0.6735
    curl https://datomic-pro-downloads.s3.amazonaws.com/1.0.6735/datomic-pro-1.0.6735.zip -O
    unzip datomic-pro-1.0.6735.zip
    rm datomic-pro-1.0.6735.zip

### Start Datomic

    java -v
    # make sure you're running java 1.8
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
