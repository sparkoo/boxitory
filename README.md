# Boxitory

is repository for Vagrant's Virtual Machine boxes, which can manage box versions and provides *Vagrant* compatible http interface. Boxes are stored on local filesystem.

Download [Latest release](https://github.com/sparkoo/boxitory/releases/latest)

For more info how it works, how to configure, ... [See Wiki](https://github.com/sparkoo/boxitory/wiki)

## Build & run

Java 11 is required

```
$ ./mvnw install && java -jar target/boxitory-{version}.jar
```
or
```
$ ./mvnw spring-boot:run
```

By default, http server will start on port *8083*.

## Docker

```
$ ./mvnw clean package docker:build docker:start
```
or
```
$ ./mvnw clean package docker:build docker:run
```

By default, container expose port *8083* with running app. Files with boxes needs to be stored in ./boxes dir.
