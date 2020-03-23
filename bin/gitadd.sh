#!/bin/bash

git add bin
git add LICENSE
git add pom.xml
git add README.md

for name in clivia-*
do
  git add $name/src
  git add $name/pom.xml
done

for name in clivia-console/ui
do
  git add $name/public
  git add $name/src
  git add $name/build.sh
  git add $name/config-overrides.js
  git add $name/package.json
  git add $name/README.md
  git add $name/yarn.lock
done