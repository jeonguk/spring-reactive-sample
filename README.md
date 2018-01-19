# spring-reactive-sample
spring boot reactive stream server sent event example

##
###

##
### Prerequisites
- JDK 1.8
- Maven 3
  - https://maven.apache.org/install.html
- GIT
  - https://git-scm.com/downloads 

## Stack
### Backend
- Spring boot 2.0.0.M7

##
### Build

```
mvn clean install
java -jar target/spring-reactive-sample-0.0.1-SNAPSHOT.jar
```

### TEST
curl -v http://localhost:8080/stock/transaction


> This API will return a response with the header Content-Type: application/stream+json

