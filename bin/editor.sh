#!/bin/bash

cd clivia-editor/ui
npm run build
cd ../..

rm -rf clivia-web/src/main/webapp/e
cp -rf clivia-editor/ui/dist clivia-web/src/main/webapp/e

rm -rf ~/tomcat/webapps/ROOT/e
cp -rf clivia-editor/ui/dist ~/tomcat/webapps/ROOT/e