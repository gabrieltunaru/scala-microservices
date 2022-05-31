# scala-microservices

## Run in docker:
To run in docker without using the source code you need to run the following commands:

```docker run --net=host -e POSTGRES_USER=ada -e POSTGRES_PASSWORD=password -d --name=database postgres:14-alpine```

this start a postgres database on port 5432. You have to wait a few minutes for it to start up before proceeding

`docker exec -i database psql -U ada  <<< "CREATE DATABASE ada_business WITH OWNER=ada ENCODING='UTF8';"`

creates the second database for the business microservice to use

`docker run --net=host -d gabrieltunaru/auth-ada` 

starts the auth service on port 8080

`docker run --net=host -d gabrieltunaru/business-ada`

starts the business microservice on port 8081
