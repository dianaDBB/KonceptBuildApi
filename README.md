# KonceptBuild API

## How to run locally

- The application loads the local `.env` file automatically. Open a terminal in the repository root and execute:

```
mvn clean package
java -jar delivery/target/delivery-1.0.0-SNAPSHOT.jar
```

If port `8443` is already in use, choose another port for this run:

```sh
SERVER_PORT=8080 java -jar delivery/target/delivery-1.0.0-SNAPSHOT.jar
```

- Or run `delivery/src/main/java/com/konceptbuild/KonceptBuildApiApplication.java` from your IDE with the repository
  root as its working directory.

## Swagger

- Local: http://localhost:8443/konceptbuild/swagger-ui/index.html#/
- Release: https://budget-cemp.onrender.com/konceptbuild/swagger-ui/index.html

## Deploy

Deploy is done using [Render](https://dashboard.render.com/)

## DB

Database is SQL, hosted on [Neon](https://console.neon.tech/).
