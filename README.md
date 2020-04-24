This is a solution to stream mocap data via internet by using Axis studio.

# Use case
There are two locations: location1 and location2. 
- The neuron hardware and software(Axis) is installed in location1. The mocap data (BVH format) captured in location1 can be streamed to location2. 
- At location2, such software as unity3D, Unreal with Neuron plugin are installed. By using neuron plugin the software can receive the mocap data in real time.

# Solution
Since location1 and location2 can't be connected directly, a server is needed to transfer the stream data between them. Both location1 and location2 need to connect to the server to upload and download data.

Below diagram shows the architecture and data flowing:

![image](https://note.youdao.com/yws/public/resource/0a2bb717f8a72a1ac060ee25d5f33347/xmlnote/E728654FF58E4B87B479AB85877DD0C5/84250)

As the diagraming show, the mocap data can be broadcasted to more than one locations.
 
The two components marked with yellow background in the diagram are the new added applications to stream the mocap data.
 1. Server: An application running in the cloud transfer the data from upclient(location1) to downclient(location2). Its IP address is knowed by apps running at location1 and location2.
 2. Upload App：A java application running at upclient side(location1). It has two functionalities:
    - Get the BVH data from Axis app via TCP protocol locally
    - Connect to Server and upload the BVH data to server

# Setup server
## Get pnstream-server app
pnstream-server app can be built from source code or just download from release page.
### build steps
1. build app,target file is: build\distributions\pnstream-server.zip
```
gradlew.bat distZip
```
2. target file is: build\distributions\pnstream-server.zip

### download page
https://github.com/neuronmocap/pns-server/releases/tag/v0.1.0

## Deploy pnstream-server app
A VPS is ready and requirements are as below
### Server Requirements
- Server hardware: 1cpu, 4G memory, 10G harddisk
- Allow the server port(9998,10000) open to public
- Server OS: centos7.4 or above
- JDK：1.8 or above

### Run pnstream-server
- scp the pnstream-server.zip to VPS
- unzip the file and run
```
/srv/pnstream-server/bin/pnstream-server
```
or run as a daemon
```
nohup /srv/pnstream-server/bin/pnstream-server &
```

The app is running as a java console. The log is as:
```
Down running at port:10000
UP running at port:9998
a TCP Up client joined:[id: 0x2b7cdb43, L:/147.8.101.137:9998 - R:/121.201.78.26:2103]
a TCP down client joined:[id: 0xb92e11d4, L:/147.8.101.137:10000 - R:/193.221.237.187:35124]
2020/04/15 03:38:19 477,viewers=1,fps=99,size=1480
2020/04/15 03:38:29 486,viewers=1,fps=102,size=1480
2020/04/15 03:38:39 486,viewers=1,fps=100,size=2960
2020/04/15 03:38:49 495,viewers=1,fps=99,size=1480
2020/04/15 03:38:59 496,viewers=1,fps=101,size=1480
2020/04/15 03:39:09 504,viewers=1,fps=101,size=1480
2020/04/15 03:39:19 514,viewers=1,fps=101,size=1480
......
```
pnstream-server is listenning at two ports: 10000 and 9998. The later is for upclient app to connect while the former is for plugin to connect.

### known issue
When up client terminated the connection, an exception as following will be printed. It doesn't matter. It can be ignored.
```
java.io.IOException: Connection reset by peer
        at sun.nio.ch.FileDispatcherImpl.read0(Native Method)
        at sun.nio.ch.SocketDispatcher.read(SocketDispatcher.java:39)
        at sun.nio.ch.IOUtil.readIntoNativeBuffer(IOUtil.java:223)
        at sun.nio.ch.IOUtil.read(IOUtil.java:192)

```
