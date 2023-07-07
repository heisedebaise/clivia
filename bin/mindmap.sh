#!/bin/bash

cd clivia-mindmap/ui
sed -i "s/^const root = .*;$/const root = '';/" src/http.js
rm -rf dist
yarn build
git checkout src/http.js
cd ../..

rm -rf clivia-web/src/main/webapp/mm
cp -rf clivia-mindmap/ui/dist clivia-web/src/main/webapp/mm

rm -rf ~/tomcat/webapps/ROOT/mm
cp -rf clivia-mindmap/ui/dist ~/tomcat/webapps/ROOT/mm