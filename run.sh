#!/usr/bin/env bash
set -e ## exit of any error
echo "Invoked $0 with $@"
echo "Running as user $(whoami)"
numOfNodes=$1
if [ "${numOfNodes}" = "" ]
then
      echo "<numOfNodes> cannot be empty.  Syntax: $0 <numOfNodes>"
      exit 1
else
      java -jar ./Solution/target/scala-2.12/grabber-1.0.jar "${numOfNodes}"
fi
