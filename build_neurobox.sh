#!/bin/bash

cd /home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/;
ls -l final-jars/
sed -i.bak 's/\(vrldir\).*/\1=\/home\/travis\/build\/NeuroBox3D\/VRL-NeuroBox-Plugin\/final-jars\//' build.properties
./gradlew build
# [[ $? -eq 0 ]] && ./gradlew installVRLPlugin
