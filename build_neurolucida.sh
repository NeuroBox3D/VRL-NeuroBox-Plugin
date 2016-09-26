#!/bin/bash

echo "Building VRL-Neurolucida-Project now"

cd $HOME/build/NeuroBox3D/VRL-NeuroBox-Plugin;
git clone https://github.com/stephanmg/VRL-Neurolucida-Project
cd VRL-Neurolucida-Project;
mkdir lib/;
cp $HOME/build/NeuroBox3D/final-jars/* lib/;
./gradlew build
./gradlew jar
du -sh $HOME/build/NeuroBox3D/VRL-NeuroBox-Plugin/VRL-Neurolucida-Project/build/libs/VRL-Neurolucida-Plugin.jar
ls -l $HOME/build/NeuroBox3D/VRL-NeuroBox-Plugin/VRL-Neurolucida-Project/build/libs/

# [[ $? -eq 0 ]] && ./gradlew installVRLPlugin
