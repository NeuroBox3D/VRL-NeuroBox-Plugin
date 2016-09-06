#!/bin/bash

cd /home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/;
sed -i.bak 's/\(vrldir\).*/\1=\/home\/travis\/build\/NeuroBox3D\/final-jars\//' build.properties
./gradlew build
# [[ $? -eq 0 ]] && ./gradlew installVRLPlugin
