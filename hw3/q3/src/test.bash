#!/bin/bash

javac Server.java
javac Client.java
java Server 4444 5555 inventory.txt
java Client localhost 4444 5555
