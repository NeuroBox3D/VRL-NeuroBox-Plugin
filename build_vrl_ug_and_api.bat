cd lib/
set ZIP_NAME="natives.zip"
7z a "%ZIP_NAME%" *.dll
set ZIP_FILE_FOLDER="%cd%

mkdir vrl-ug
git clone https://github.com/VRL-Studio/VRL-UG
git clone https://github.com/VRL-Studio/VRL
echo "%cd%";
cd C:\projects\vrl-neurobox-plugin\lib\VRL\VRL\
call ant clean
call ant compile
call ant jar
cd ..\
cd ..\

set VRL_UG_PACKAGE_NATIVES="eu\mihosoft\vrl\plugin\content\natives\"
set COMMON_PART_NATIVES="src\%VRL_UG_PACKAGE_NATIVES%"

cd C:\projects\vrl-neurobox-plugin\lib\VRL-UG\VRL-UG
md "%COMMON_PART_NATIVES%\windows\x86"
md "%COMMON_PART_NATIVES%\windows\x64"
xcopy "%ZIP_FILE_FOLDER%\%ZIP_NAME%" "%COMMON_PART_NATIVES%\windows\x86\%ZIP_NAME%"
xcopy "%ZIP_FILE_FOLDER%\%ZIP_NAME%" "%COMMON_PART_NATIVES%\windows\x64\%ZIP_NAME%"
xcopy C:\projects\vrl-neurobox-plugin\lib\VRL\VRL\dist\VRL.jar C:\projects\vrl-neurobox-plugin\lib\VRL-UG\VRL-UG\jars\
call ant clean
call ant compile
call ant jar

cd ..\
cd ..\
md console-app;
cd console-app;
xcopy C:\projects\vrl-neurobox-plugin\vrl-app-for-github.zip "%cd%"
call 7z e vrl-app-for-github.zip -y
cd ugInit-consolApp\
xcopy C:\projects\vrl-neurobox-plugin\lib\VRL\VRL\dist\VRL.jar ugInit-consolApp\.application\lib\
xcopy C:\projects\vrl-neurobox-plugin\lib\VRL-UG\VRL-UG\dist\VRL-UG.jar ugInit-consolApp\.application\property-folder\plugin-updates\
xcopy C:\projects\vrl-neurobox-plugin\lib\VRL-UG\VRL-UG\dist\VRL-UG.jar ugInit-consolApp\.application\property-folder\plugins\

call C:\project\vrl-neurobox-plugin\run.bat
call C:\project\vrl-neurobox-plugin\run.bat
set BASEPATH=C:\projects\vrl-neurobox-plugin\lib\VRL-UG\VRL-UG\jars

xcopy %BASEPATH%\apache-xmlrpc-3.1.3\lib\commons-logging-1.1.jar ugInit-consolApp\.application\lib\
xcopy %BASEPATH%\apache-xmlrpc-3.1.3\lib\ws-commons-util-1.0.2.jar ugInit-consolApp\.application\lib
xcopy %BASEPATH%\apache-xmlrpc-3.1.3\lib\xmlrpc-client-3.1.3.jar ugInit-consolApp\.application\lib
xcopy %BASEPATH%\apache-xmlrpc-3.1.3\lib\xmlrpc-common-3.1.3.jar ugInit-consolApp\.application\lib
xcopy %BASEPATH%\apache-xmlrpc-3.1.3\lib\xmlrpc-server-3.1.3.jar ugInit-consolApp\.application\lib
call C:\project\vrl-neurobox-plugin\run.bat

echo "ADAPT / DEBUG BELOW FOR WINDOWS"

echo "VRL-UG-API information:"
file /home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/console-app/ugInit-consolApp/.application/property-folder/plugins/VRL-UG-API.jar
du -sh /home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/console-app/ugInit-consolApp/.application/property-folder/plugins/VRL-UG-API.jar

cd /home/travis/build/NeuroBox3D/;
mkdir "final-jars/"; cd "final-jars"/;
cp /home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL-UG/VRL-UG/dist/VRL-UG.jar .
cp /home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/console-app/ugInit-consolApp/.application/property-folder/plugins/VRL-UG-API.jar .
cp /home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL/VRL/dist/VRL.jar .
cp $BASEPATH/apache-xmlrpc-3.1.3/lib/commons-logging-1.1.jar .
cp $BASEPATH/apache-xmlrpc-3.1.3/lib/ws-commons-util-1.0.2.jar .
cp $BASEPATH/apache-xmlrpc-3.1.3/lib/xmlrpc-client-3.1.3.jar .
cp $BASEPATH/apache-xmlrpc-3.1.3/lib/xmlrpc-common-3.1.3.jar .
cp $BASEPATH/apache-xmlrpc-3.1.3/lib/xmlrpc-server-3.1.3.jar .
