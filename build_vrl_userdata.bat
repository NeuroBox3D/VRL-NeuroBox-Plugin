git clone https://github.com/VRL-Studio/VRL-UserData
cd VRL-UserData
xcopy C:\projects\vrl-neurobox-plugin\final-jars\VRL-UG.jar jars\
xcopy C:\projects\vrl-neurobox-plugin\final-jars\VRL-UG-API.jar jars\
xcopy C:\projects\vrl-neurobox-plugin\final-jars\VRL.jar jars\
call ant clean
call ant compile
call ant jar
xcopy dist\VRL-UserData.jar C:\projects\vrl-neurobox-plugin\final-jars\
