#!/bin/bash
# 0. build UG (in .travis.yml)
# 1. build VRL
# 2. build VRL-UG with natives
# 3. build VRL-UG-API (todo - get therefore project template from external website)
# 4. build VRL-UserData (todo)
# 5. build VRL-NeuroBox-Plugin (todo)

ZIP_NAME="natives.zip"
cd lib/
zip -r "${ZIP_NAME}" *.so
mkdir -p vrl_natives/linux/x86

mkdir vrl-ug
git clone https://github.com/VRL-Studio/VRL-UG
git clone https://github.com/VRL-Studio/VRL
cd VRL/VRL/;
ant clean; ant test; ant jar
cd ../../;

cd VRL-UG/VRL-UG/
cp /home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL/VRL/dist/VRL.jar jars/
ant clean; ant compile; 
ant jar



cd ../../;
mkdir console-app;
cd console-app;
wget http://www.stephangrein.de/files/vrl/vrl-app-for-github.zip
unzip vrl-app-for-github.zip &> /dev/null
cd ugInit-consolApp/;
chmod +x run.sh;
#cp /home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL/VRL/dist/VRL.jar /home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/console-app/ugInit-consolApp/.application/lib/

rm -rf /home/travis/.vrl/0.4.2/default/plugins/VRL-UG*;
rm -rf .application/property-folder/plugins/unzipped/VRL-UG*;
rm -rf .application/proeprty-folder/plugins/VRL-UG*;
cp /home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL-UG/VRL-UG/dist/VRL-UG.jar .application/property-folder/plugin-updates/
cp /home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL-UG/VRL-UG/dist/VRL-UG.jar /home/travis/.vrl/0.4.2/default/plugins/

# install vrl-ug
./run.sh; 
# build vrl-ug-api
# ./run.sh;
