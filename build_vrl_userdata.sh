#!/bin/bash

cd /home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/;
ls -l final-jars/
git clone https://github.com/VRL-Studio/VRL-UserData
cd VRL-UserData;
cd /home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/final-jars/* jars/;
ant clean
ant compile
ant jar
cp dist/VRL-UserData.jar /home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/final-jars/
