# Instructions to deploy a PostgreSQL database and pgAdmin using docker compose
This docker-compose file while build two Docker images:
- A PostgreSQL database
- A pgAdmin database manager to work with PostgreSQL
 
## Requirements
- Docker compose installed on your computer.
- [This docker compose](docker-compose.yml) file in one of the folders of your computer.

## Build docker compose
Open the command line interface on your OS and go to the folder where the docker compose file 
is and execute:

```$ docker compose up -d```

## Stop the docker compose
To end the process and remove the docker images execute:

```$ docker compose down```

## Troubleshooting
A common error trying to build the docker compose happens when some port is already being used. 
Make sure to stop the process using that port and try again.

If the postgreSQL port is being used, for example, you can execute this command:

```$ sudo ss -lptn 'sport = :5432'```

And then kill it executing

```$ sudo kill {pid}```

where {pid} is the pid of the process using the port

