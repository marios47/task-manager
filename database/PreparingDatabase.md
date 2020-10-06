# Preparing PostgreSQL database
To access the PostgreSQL database you can use the postgresql CLI, the pgAdmin imagen from 
the docker-compose ([check the instructions](DockerComposeInstructions.md)) or your own database manager.
In this example **I will use the pgAdmin from the docker compose file**. 

## Create the server
Click on Object/Create/Server... and fill the following properties:

- Host name/address: **pgsql-server**
- Port: **5432**
- Maintenance database: **postgres**
- Username: **admin**
- Password: **secret**

## Create the table
Open the Query Tool (Tools/Query Tool), insert the following SQL sentence to create the table and execute it (F5):

```
CREATE TABLE tasks(
  id SERIAL PRIMARY KEY,
  name VARCHAR (50) NOT NULL,
  description VARCHAR (250),
  finished BOOLEAN NOT NULL DEFAULT FALSE
 );
```

Also add an **INDEX** on the field 'name' to make queries filtered by name much faster:

```
CREATE INDEX name_index ON tasks (name);
```  