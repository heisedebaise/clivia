#!/bin/bash

rm -rf build
sed -i "s/^const root = .*;$/const root = '';/" src/http.js
yarn build
git checkout src/http.js
# rm -rf ../../clivia-web/src/main/webapp/c
# cp -rf build ../../clivia-web/src/main/webapp/c