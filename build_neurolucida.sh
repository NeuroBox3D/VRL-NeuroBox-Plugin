#!/bin/bash

echo "Building VRL-Neurolucida-Project now"

cd /home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin;
git clone https://github.com/stephanmg/VRL-Neurolucida-Project
cd VRL-Neurolucida-Project;
mkdir lib/;
cp /home/travis/build/NeuroBox3D/final-jars/* lib/;
./gradlew build
# [[ $? -eq 0 ]] && ./gradlew installVRLPlugin
