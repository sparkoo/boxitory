# Boxitory

is repository for Vagrant's Virtual Machine boxes, which can manage box versions and provides *Vagrant* compatible http interface.

## Build & run

`./mvnw install && java -jar target/boxitory-{version}.jar`

By default, http server will start on port *8083*.

## How it works

*Boxitory* currently implements just filesystem box provider. That requires strict folder structure.

### Box files on filesystem

There must be one home folder for all boxes with subfolders for each box type. Individial box versions must be named `{name}_{version}_{provider}.box`. 

See example below:
```
$ tree test_repository/
test_repository/
├── f25
│   ├── f25_1_virtualbox.box
│   └── f25_2_virtualbox.box
├── f26
│   ├── f26_1_virtualbox.box
│   ├── f26_2_virtualbox.box
│   └── f26_3_virtualbox.box
```

### Http interface

Server starts at port *8083* and boxes can be requested on `http://hostname:port/box_name` for example:
```
$ curl http://localhost:8083/f26
{
  "name": "f26",
  "description": "f26",
  "versions": [
    {
      "version": "1",
      "providers": [
        {
          "url": "sftp://my_box_server:/tmp/test_repository/f26/f26_1_virtualbox.box",
          "name": "virtualbox"
        }
      ]
    },
    {
      "version": "2",
      "providers": [
        {
          "url": "sftp://my_box_server:/tmp/test_repository/f26/f26_2_virtualbox.box",
          "name": "virtualbox"
        }
      ]
    },
  ]
}
```

## Configuration

### Options
 * `box.home`
   * path where to find boxes
   * in example above, it will be set to `/tmp/test_repository`
   * **default value**: `.`
 * `box.prefix` 
   * prefix for the output json, that is prepend before absolute local path of the box
   * do define for example protocol or server, where boxes are placed
   * e.g.: `sftp://my_box_server:`
   * **default value**: *empty*

Configuration can be provided by `application.properties` file on classpath
```
# application.properties
box.home=/tmp/test_repository
box.prefix=sftp://my_box_server:
```
or as command line arguments `java -jar -Dbox.home=/tmp/test_repository target/boxsitory-${version}.jar`