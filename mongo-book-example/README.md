Your Bash command:

docker compose exec mongodb mongo -u admin -p secret --authenticationDatabase admin

This is the most reliable PowerShell version.

Running jdbc in memory flapdoodle mongo db test - 

mvn test -Dtest=MongoOpsBooksNTest  "-Dspring.profiles.active=test"

Running jdbc mongodb remote test - 

Step-1 run mongodb in docker : docker compose up mongodb -d

Step-2: mvn test -Dtest=MongoOpsBooksNTest  "-Dspring.profiles.active=mongodb"

Step-3 stop socker container for mongodb : docker compose down

Generated Schema Output
$ mvn clean package spring-boot:start spring-boot:stop "-Dspring-boot.run.profiles=mongodb" -DskipTests 
$ docker compose up mongodb -d 
