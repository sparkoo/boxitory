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

#### Build status (travis-ci)

devel [![Build Status](https://travis-ci.org/sparkoo/boxitory.svg?branch=devel)](https://travis-ci.org/sparkoo/boxitory) 
[![codecov](https://codecov.io/gh/sparkoo/boxitory/branch/devel/graph/badge.svg)](https://codecov.io/gh/sparkoo/boxitory)


master: [![Build Status](https://travis-ci.org/sparkoo/boxitory.svg?branch=master)](https://travis-ci.org/sparkoo/boxitory)


