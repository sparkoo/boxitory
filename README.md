# Boxitory

is repository for Vagrant's Virtual Machine boxes, which can manage box versions and provides *Vagrant* compatible http interface. Boxes are stored on local filesystem.

Download [Latest release](https://github.com/sparkoo/boxitory/releases/latest)

For more info how it works, how to configure, ... [See Wiki](https://github.com/sparkoo/boxitory/wiki)

## Build & run

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
#### Build status (travis-ci)

devel [![Build Status](https://travis-ci.org/sparkoo/boxitory.svg?branch=devel)](https://travis-ci.org/sparkoo/boxitory) 
[![codecov](https://codecov.io/gh/sparkoo/boxitory/branch/devel/graph/badge.svg)](https://codecov.io/gh/sparkoo/boxitory)
[![codebeat badge](https://codebeat.co/badges/40f8804c-f98d-4f2c-958a-737c901fa5fe)](https://codebeat.co/projects/github-com-sparkoo-boxitory-devel)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/4bdd9bd53659424e96c4119d9c8fa7fc)](https://www.codacy.com/app/sparkoo/boxitory?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=sparkoo/boxitory&amp;utm_campaign=Badge_Grade)


master: [![Build Status](https://travis-ci.org/sparkoo/boxitory.svg?branch=master)](https://travis-ci.org/sparkoo/boxitory)


