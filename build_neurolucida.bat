git clone https://github.com/stephanmg/VRL-Neurolucida-Project
cd VRL-Neurolucida-Project
mkdir lib\
xcopy C:\projects\vrl-neurobox-plugin\lib\VRL\VRL\dist\VRL.jar lib\
xcopy C:\projects\vrl-neurobox-plugin\lib\VRL-UG\VRL-UG\dist\VRL-UG.jar lib\
xcopy C:\projects\vrl-neurobox-plugin\lib\ugInit-consolApp\.application\property-folder\plugins\VRL-UG-API.jar lib\
CALL C:\projects\vrl-neurobox-plugin\gradlew.bat build
CALL C:\projects\vrl-neurobox-plugin\gradlew.bat jar
