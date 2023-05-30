# Reward Points
Recruitment process project

## Prerequisites
To run application, Maven, Java 17 and Docker should be installed on the machine

## How to run
Step 1. Build the project using maven at main application directory:
```shell
mvn clean package
```

Step 2. Run dockerized app and database using provided console command
```shell
docker-compose up
```
NOTE_1: keep in mind that using the provided command implies the 8080 and 3306 port is available 

## Usage
You can check what endpoints are available via 
`` http://localhost:8080/swagger-ui/index.html``

## Assumptions
Below list of assumptions that were taken into consideration during development:
1. Application is unaware if given user exists. If no transactions found for user response will be empty.
2. Transactions can only be added one by one
3. A reward point is provided for every integer, fractions are rounded down
4. There is a possibility to update transaction as a whole including userId and createdAt


## Additional Info
- Time of development: approx. 10-12 hrs
- Test coverage for lines: approx. 95%
