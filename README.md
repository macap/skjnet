# skjnet

Java application for transferring files using pure java.net sockets. Supports multiple "hosts" and simultaneus downloading (one file form multiple sources).
It's only for educational purposes - created as a project for "Computer Networks" course in PJATK. 

The purpose of this app was to learn how to use sockets in java. Also i've learned how various application protocols 
work and even created my own (similar to HTTP) to use in this application.

It still needs some fixes - some exceptions are not handled properly, 
some config values are hardcoded (i.e. working directory), but it serves it's purpose.

## Protocol

SKJNET protocol is inspired by how HTTP works. General structure of a message is:

```
SKJNET <command>
<header_key>:<header_value>
<header_key>:<header_value>
<empty_line>
<data>
```

It supports three commands: LIST, GET, PUSH

####LIST

Request file list from a server

Request | Response
--- | --- 
_SKJNET LIST_ | _SKJNET OK_
 | _Plik.txt D90W8E6F65V5V5DSHWKLOF 12345_

Response data is formatted: <filename> <md5-sum> <size in bytes>

####GET

Requests file from a server

Request | Response
--- | --- 
_SKJNET GET_ | _SKJNET OK_
_File:Plik.txt_ | _name:Plik.txt_
_Range:0-_ | _size:12345_
 | _length:12345_
 | _range:0-12345_
 | _sum:D90W8E6F65V5V5DSHWKLOF_
 | 
 | file_contents
 
 
####PUSH

Pushes file to the server

Request | Response
--- | --- 
_SKJNET GET_ | no response
_File:Plik.txt_ | 
_size:12345_ | 
_sum:D90W8E6F65V5V5DSHWKLOF_ | 
 | 
file_contents |

## Http server

An simple HTTP server is included, where you can find app logs. 


## How to run this app

Example: Running multiple instances in Windows with basic (default) configuration:

This will run 4 instances with file transfer on ports 10001-10004 and http interfaces on 11001-11004.
It requires 4 directories on D: drive to exist named TORrent_`{instanceId}` (or in home dir on *nix systems; i.e. D:\TORrent_1, D:\TORrent_2 ...).

```
start java -cp bin skjnet.Mian 1 4
start java -cp bin skjnet.Mian 2 4
start java -cp bin skjnet.Mian 3 4
start java -cp bin skjnet.Mian 4 4
```

### Supported start arguments

```Arguments: instanceId <instancesCount=2> <hostname=127.0.0.1>```

---

_Aplikacja powstała w ramach procesu edukacyjnego w PJATK_
