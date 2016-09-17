git clone https://github.com/VRL-Studio/VRL-UserData
cd VRL-UserData
xcopy C:\projects\vrl-neurobox-plugin\lib\VRL\VRL\dist\VRL.jar jars\
xcopy C:\projects\vrl-neurobox-plugin\lib\VRL-UG\VRL-UG\dist\VRL-UG.jar jars\
xcopy C:\projects\vrl-neurobox-plugin\lib\ugInit-consolApp\.application\property-folder\plugins\VRL-UG-API.jar jars\
xcopy C:\projects\vrl-neurobox-plugin\final-jars\VRL-UserData.jar jars\
call ant clean
call ant compile
call ant jar
xcopy dist\VRL-UserData.jar C:\projects\vrl-neurobox-plugin\final-jars\
