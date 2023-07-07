#!/bin/bash

cd clivia-mindmap/ui
sed -i "s/^const root = .*;$/const root = '';/" src/http.js
rm -rf dist
yarn build
git checkout src/http.js
cd ../..

rm -rf clivia-web/src/main/webapp/e
cp -rf clivia-mindmap/ui/dist clivia-web/src/main/webapp/e

rm -rf ~/tomcat/webapps/ROOT/e
cp -rf clivia-mindmap/ui/dist ~/tomcat/webapps/ROOT/e