git clone https://github.com/VRL-Studio/VRL-UserData
cd VRL-UserData
xcopy C:\projects\vrl-neurobox-plugin\final-jars\* %cd%
ant clean
ant compile
ant jar
xcopy dist\VRL-UserData.jar C:\projects\vrl-neurobox-plugin\final-jars\
