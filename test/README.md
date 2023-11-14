# Component Testing The Component Test Framework

## Running The Tests

Switch to ctf-test module directory:
```
cd ctf-test
```

Build the test Spring Boot application jar:
```
mvn clean install
```

Build Docker container:
```
docker build -t ct/ctf-test:latest .
```

Run tests:
```
mvn test -Pcomponent
```

Run tests leaving containers up:
```
mvn test -Pcomponent -Dcontainers.stayup
```

## Clean Up Commands

- Manual clean up (if left containers up):
```
docker rm -f $(docker ps -aq)
```

- Forceful clean up (if Docker problems):
```
docker network prune
```
```
docker system prune
```
