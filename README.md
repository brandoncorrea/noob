# noob

A Discord bot designed to... well, that part is up to you.

## Setup

### Java

Java 8 is required for various reasons:

1. Database operations (`clj -Mmigrate:test`) will fail on later versions of Java.
2. TODO: Find the reasons and update to Java 13+ when possible.

[How to Install Java 8 on Mac w/ HomeBrew](https://stackoverflow.com/a/28635465)

### Leiningen

    brew install leiningen

## Config

Create `config.edn` in the same directory as `project.clj` with the following 
structure and add your Discord bot's token:

````clojure
{:token "BOT_TOKEN"}
````

## Database

### Download

    # Download and unzip datomic-pro 1.0.6269
    unzip datomic-pro-1.0.6269

### Start Datomic

    java -v
    # make sure you're running java 1.8
    bin/db

### Schema / Migration

    # migrate to ensure database schema matches code
    clj -Mmigrate:test

## Running Server Locally

Make sure datomic is running...

    clj -Mrun

## Running Tests

    # clojure specs:
    clj -Mspec:test
    # clojure specs automatically running when fileds are changed:
    clj -Mspec:test -a

## Deployment

Use the infrastructure repo
