![EU Regional Development Fund](docs/img/EL_Regionaalarengu_Fond_horisontaalne-vaike.jpg)

# Integration Tests for SiGa (Signature Gateway)

## Prerequisites

* Java 11 JDK
* [Signature Gateway](https://github.com/open-eid/SiGa) running in local or remote environment

## Running tests

Modify the file [application-test.properties](src/test/resources/application-test.properties) according to the
environment where SiGa is running at. If you are running SiGa in local docker-compose setup as described at
https://github.com/open-eid/SiGa#running-siga-with-docker, then the default configuration should work.

Execute the command:
```bash
./mvnw clean verify
```
