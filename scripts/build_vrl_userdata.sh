#!/bin/bash

cd $HOME/build/NeuroBox3D/VRL-NeuroBox-Plugin/;
git clone https://github.com/VRL-Studio/VRL-UserData
cd VRL-UserData;
cp $HOME/build/NeuroBox3D/final-jars/* jars/;
ls -l $HOME/build/NeuroBox3D/final-jars/;
ant clean
ant compile
ant jar
cp dist/VRL-UserData.jar $HOME/build/NeuroBox3D/final-jars/
