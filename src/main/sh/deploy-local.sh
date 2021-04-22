#!/bin/bash

sudo service tomcat stop
sudo rm -rf /var/lib/tomcat/webapps/ROOT* /data/mirrir/xnat_home_mirrir-dev-maffitt1.nrg.wustl.edu/logs/*
cp xnat-web-dcmWeb-1.8.0-SNAPSHOT.war /tmp
chmod a+r /tmp/xnat-web-dcmWeb-1.8.0-SNAPSHOT.war
sudo cp /tmp/xnat-web-dcmWeb-1.8.0-SNAPSHOT.war /var/lib/tomcat/webapps/ROOT.war
sudo chown nrg-svc-mirrir:nrg-mirrir-fs /var/lib/tomcat/webapps/ROOT.war
sudo service tomcat start