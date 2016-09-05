#!/bin/bash

cd $HOME;
git checkout master
git pull
sed -i.bak 's/\(vrldir\).*/\1=\/home\/travis\/build\/NeuroBox3D\/VRL-NeuroBox-Plugin\/final-jars\//' build.properties
./gradlew build
# [[ $? -eq 0 ]] && ./gradlew installVRLPlugin
git stash
exit 0
