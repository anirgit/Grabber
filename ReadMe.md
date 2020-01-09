# Modules

* **`MockService`**
* **`Solution`**

# Module Details

## MockService

This is a dummy service that acts as a node itself. It exposes a REST GET endpoint and returns random kb usage info about a random node.
There is also a random seconds of sleep introduced to make the node randomly take more time in responding back after receiving the http request.

## Solution

This module receives total number of nodes and then calls individual node's end point in parallel thread. For each node, it starts an akka stream that calls the node
every 5 seconds and then stored either a new record in usage_data table for the 1st time or inserts a new record with delta kb usage. The http connection is configured
to time out after 2 secs of idle time. We log the time out, let the stream recover from failure and try to call the end point again for the same node.

## Run Instructions
If the MockService needs to be available on localhost, then go to `/Grabber` folder location and issue command
```
 sbt mockService/run
 ```
 Once mockService module is up, then from `/Grabber` folder run the shell script
 ```
 ./run.sh <desired number of nodes>
```