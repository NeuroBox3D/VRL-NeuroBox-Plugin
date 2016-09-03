#!/bin/bash
# 1. build VRL
# 2. build VRL-UG with natives
# 3. build VRL-UG-API (todo - get therefore project template from external website)
# 4. build VRL-NeuroBox-Plugin (todo)

ZIP_NAME="natives.zip"
cd lib/
zip -r "${ZIP_NAME}" *.so
mkdir -p vrl_natives/linux/x86

mkdir vrl-ug
git clone https://github.com/VRL-Studio/VRL-UG
git clone https://github.com/VRL-Studio/VRL
cd VRL/VRL/;
ant clean; ant test; ant jar
ls -l *;
cd ../../;

cd VRL-UG/VRL-UG/
cp /home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL/VRL/dist/VRL.jar jars/
ant clean; ant compile
