#!/bin/bash

echo "Stop Tomcat"
sudo systemctl stop tomcat9.service
sudo rm -rf /var/lib/tomcat9/webapps/ROOT* /data/xnat/home/logs/*
sudo cp xnat-web-dcmWeb-0.6-SNAPSHOT.war /var/lib/tomcat9/webapps/ROOT.war
sudo chown xnat:xnat /var/lib/tomcat9/webapps/ROOT.war

echo "Starting Tomcat..."
sudo systemctl start tomcat9.service