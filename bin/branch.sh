#!/bin/bash

version=0.2
git checkout -b $version
source bin/gitadd.sh
git commit -m $version
git push origin $version
git checkout master
