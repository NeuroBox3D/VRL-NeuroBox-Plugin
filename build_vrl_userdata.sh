#!/bin/bash

cd /home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/;
git clone https://github.com/VRL-Studio/VRL-UserData
cd VRL-UserData;
cp /home/travis/build/NeuroBox3D/final-jars/* jars/;
ls -l /home/travis/build/NeuroBox3D/final-jars/;
ant clean
ant compile
ant jar
cp dist/VRL-UserData.jar /home/travis/build/NeuroBox3D/final-jars/
