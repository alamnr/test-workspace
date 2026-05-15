Your Bash command:

docker compose exec -T postgres psql -U postgres \
< .../src/main/resources/db/migration/V1.0.0_0__initial_schema.sql

uses input redirection (<), which works slightly differently in PowerShell.

Here are the correct PowerShell equivalents.

Option 1 — Recommended PowerShell Equivalent

Use Get-Content piped into the container:

Get-Content src/main/resources/db/migration/V1.0.0_0__initial_schema.sql |
docker compose exec -T postgres psql -U postgres

Get-Content src/main/resources/db/migration/V1.0.0_1__initial_indexes.sql |
docker compose exec -T postgres psql -U postgres

This is the most reliable PowerShell version.

Running jdbc in memory db test - 

mvn test -Dtest=JdbcSongsNTest  "-Dspring.profiles.active=test"

Running jdbc postgres test - 

Step-1 run postgres in docker : docker compose up postgres -d

Step-2: mvn test -Dtest=JdbcSongsNTest  "-Dspring.profiles.active=postgres"

Step-3 stop socker container for postgres : docker compose down

Generated Schema Output
$ docker compose up postgres -d 
$ mvn clean package spring-boot:start spring-boot:stop "-Dspring-boot.run.profiles=postgres" -DskipTests 
