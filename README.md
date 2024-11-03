## microservices-demo

### Overview
_Java_ + _Spring Boot_ microservices implementation demo.

REST API and _Kafka_ used for apps communication. _Postgres_ used as database.

Each service distributes REST/Kafka contracts for communication as the separate module.

_Swagger_ used to visualize OpenAPI specifications.

In-memory DB/Kafka implementations used for tests.

Each microservice packaged to the _Docker_ container.

Basic logging tracing and monitoring implemented. 
_Logback_ + _Loki_ + _Grafana_ used to collect and display logs.
_Logbook_ used for HTTP request and response logging

_Actuator_ API endpoints added to each microservice.
_Prometheus_ + _Tempo_ + _Grafana_ used for tracing.

_GitlabCI_ used to build the project, monorepo approach used with the corresponding pipeline which triggers
child pipelines per each microservice if the code has been changed there.

Artifacts (docker images and .jar contracts) published to _Nexus_ artifacts storage.

_SonarQube_ performs static code analysis and collects tests coverage.

_Kafka UI_ used to view/manage kafka cluster. 

__All infrastructure configuration and microservices env defined in docker-compose 
files and included as a part of this repo__. But it was designed for local dev environment, not production.

#### Disclaimer
This project has been implemented in self-education purposes by the person who never developed microservices before.

This project has been implemented withing the limited timeframe 2-3 weeks I've spent ~ 16hours per week, 
so the whole development and infra setup took ~ 40hours.

There was local bare metal CPU/RAM limitations: 8CPU + 32GB RAM, cloud resources wasn't used.

_Known missing things_:
    
- Roles and permissions, Keycloack + spring-security
- More advanced CI pipelines with handling release versioning, release version incrementation per service, 
running released app on dev env + run more e2e system tests
- More automated tests might be added, especially e2e system tests
- Tracing might be improved to link rest->kafka and show the whole flow
- Kubernetes could be utilized instead of docker-compose
- Overall infrastructure was designed for local dev environment, not production


### Project details

There are 3 applications aimed to generate image with some text and manage generated results.

#### 1 image-generation-manager
Sends generate commands to __image-generator__, tracks the generation process state
    
![image-generation-manager.png](.assets/image-generation-manager.png)

#### 2 image-generator
Generates images by request, uploads generations to __generated-files-storage__, 
notifies __image-generation-manager__ when generation finished/error

![kafka-topic-generation-requests.png](.assets/kafka-topic-generation-requests.png)
![kafka-topic-image-generator-events.png](.assets/kafka-topic-image-generator-events.png)

#### 3 generated-files-storage
Exposes api to upload file for storage, and get endpoint to download generated files by id

![generated-files-storage.png](.assets/generated-files-storage.png)  
![get-file-by-id.png](.assets/get-file-by-id.png)

### Infrastructure details

#### Gitlab CI

__Runner__

![gitlab-ci-runner.png](.assets/gitlab-ci-runner.png)

__Repo__

![gitlab-ci-repository.png](.assets/gitlab-ci-repository.png)

__CI Pipelines__

![gitlab-ci-monorepo-pipeline.png](.assets/gitlab-ci-monorepo-pipeline.png)

#### SonarQube

```text
http://dev-cluster:9000/projects
```
![sonar-qube-projects-list.png](.assets/sonar-qube-projects-list.png)

```text
http://dev-cluster:9000/dashboard?id=image-generator
```
![sonar-qube-project-details.png](.assets/sonar-qube-project-details.png)

#### Nexus

__Maven__
```text
http://dev-cluster:8099/#browse/browse:maven-snapshots:dmax
```
![nexus-maven-snapshots.png](.assets/nexus-maven-snapshots.png)

__Docker__

```text
http://dev-cluster:8099/#browse/browse:docker
```
![nexus-docker-browse.png](.assets/nexus-docker-browse.png)

![nexus-docker-setup.png](.assets/nexus-docker-setup.png)

### Tracing / Observability

#### Grafana
__Spring Boot 3.x Statistics Dashboard__
```text
http://dev-cluster:3000/d/spring_boot_21/spring-boot-3-x-statistics?orgId=1
```
![grafana-spring-boot-dashboard.png](.assets/grafana-spring-boot-dashboard.png)

__Logs__
```text
http://dev-cluster:3000/explore?schemaVersion=1&panes=%7B%22nqv%22:%7B%22datasource%22:%22loki%22,%22queries%22:%5B%7B%22refId%22:%22A%22,%22expr%22:%22%7Bservice_name%3D%5C%22image-generation-manager%5C%22%7D%20%7C%3D%20%60%60%22,%22queryType%22:%22range%22,%22datasource%22:%7B%22type%22:%22loki%22,%22uid%22:%22loki%22%7D,%22editorMode%22:%22builder%22%7D%5D,%22range%22:%7B%22from%22:%22now-5m%22,%22to%22:%22now%22%7D%7D%7D&orgId=1
```
![grafana-loki-logs.png](.assets/grafana-loki-logs.png)

__Tracing__
```text
http://dev-cluster:3000/explore?schemaVersion=1&panes=%7B%22cbg%22:%7B%22datasource%22:%22tempo%22,%22queries%22:%5B%7B%22refId%22:%22A%22,%22datasource%22:%7B%22type%22:%22tempo%22,%22uid%22:%22tempo%22%7D,%22queryType%22:%22traceql%22,%22limit%22:20,%22tableType%22:%22traces%22,%22query%22:%22672723ced6d595e5a5b8f73f47ff7dd5%22%7D%5D,%22range%22:%7B%22from%22:%22now-1h%22,%22to%22:%22now%22%7D%7D%7D&orgId=1
```
![grafana-tempo-tracing-1.png](.assets/grafana-tempo-tracing-1.png)

```text
http://dev-cluster:3000/explore?schemaVersion=1&panes=%7B%22cbg%22:%7B%22datasource%22:%22tempo%22,%22queries%22:%5B%7B%22refId%22:%22A%22,%22datasource%22:%7B%22type%22:%22tempo%22,%22uid%22:%22tempo%22%7D,%22queryType%22:%22traceql%22,%22limit%22:20,%22tableType%22:%22traces%22,%22query%22:%22672723ce116764a3d047c3ba2b5dff66%22%7D%5D,%22range%22:%7B%22from%22:%221730610697157%22,%22to%22:%221730625097157%22%7D%7D%7D&orgId=1
```

![grafana-tempo-tracing-2.png](.assets/grafana-tempo-tracing-2.png)


#### Kafka UI

![kafka-ui-consumers.png](.assets/kafka-ui-consumers.png)

![kafka-ui-topics.png](.assets/kafka-ui-topics.png)

![kafka-ui-event.png](.assets/kafka-ui-event.png)