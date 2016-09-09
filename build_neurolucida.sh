#!/bin/bash

echo "Building VRL-Neurolucida-Project now"

cd /home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin;
git clone https://github.com/stephanmg/VRL-Neurolucida-Project
cd VRL-Neurolucida-Project;
mkdir lib/;
cp /home/travis/build/NeuroBox3D/final-jars/* lib/;
./gradlew build
./gradlew jar
du -sh /home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/VRL-Neurolucida-Project/build/libs/VRL-Neurolucida-Project.jar

# [[ $? -eq 0 ]] && ./gradlew installVRLPlugin
