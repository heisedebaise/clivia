#!/bin/bash

source bin/install.sh

podman stop tomcat
rm -rf ~/tomcat/webapps/ROOT
cp -r clivia-web/target/clivia-web-0.3 ~/tomcat/webapps/ROOT
podman restart tomcat
tail -f ~/tomcat/logs/catalina.out
