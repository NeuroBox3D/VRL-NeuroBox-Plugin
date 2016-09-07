#!/bin/bash

cd /home/travis/build/NeuroBox3D/VRL-Neurolucida-Project/;
git clone https://github.com/stephanmg/VRL-Neurolucida-Project
mkdir lib/;
cp /home/travis/build/NeuroBox3D/final-jars/* lib/;
./gradlew build
## [[ $? -eq 0 ]] && ./gradlew installVRLPlugin
