#!/bin/bash

sudo service tomcat7 stop
sudo rm -rf /var/lib/tomcat7/webapps/ROOT* /data/xnat/home/logs/*
sudo cp xnat-web-dcmWeb-1.8.0-SNAPSHOT.war /var/lib/tomcat7/webapps/ROOT.war
sudo chown xnat:xnat /var/lib/tomcat7/webapps/ROOT.war
sudo service tomcat7 start