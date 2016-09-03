#!/bin/bash

ZIP_NAME="natives.zip"
cd lib/
zip -r "${ZIP_NAME}" *.so
mkdir -p vrl_natives/linux/x86

mkdir vrl-ug
git clone https://github.com/VRL-Studio/VRL-UG
git clone https://github.com/VRL-Studio/VRL
cd VRL/VRL/;
ant clean; ant 
cd ../../;

cd VRL-UG/VRL-UG/
cp ../../VRL/VRL/dist/VRL.jar jars/
ant clean; ant compile