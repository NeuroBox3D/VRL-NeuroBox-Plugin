cd lib/
set ZIP_NAME="natives.zip"
echo "%cd%"
copy C:\projects\vrl-neurobox-plugin\bin\ug4.dll libug4.dll
7z a "%ZIP_NAME%" libug4.dll
set ZIP_FILE_FOLDER="%cd%"

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
echo "CREATE DIRECTORIES"
md "%COMMON_PART_NATIVES%\windows\x86\"
md "%COMMON_PART_NATIVES%\windows\x64\"
xcopy "%ZIP_FILE_FOLDER%\%ZIP_NAME%" "%COMMON_PART_NATIVES%\windows\x86\"
xcopy "%ZIP_FILE_FOLDER%\%ZIP_NAME%" "%COMMON_PART_NATIVES%\windows\x64\"
echo "COPIED natives into folder."
xcopy C:\projects\vrl-neurobox-plugin\lib\VRL\VRL\dist\VRL.jar C:\projects\vrl-neurobox-plugin\lib\VRL-UG\VRL-UG\jars\
call ant clean
call ant compile
call ant jar

cd ..\
cd ..\
md console-app;
cd console-app;
xcopy C:\projects\vrl-neurobox-plugin\vrl-app-for-github.zip "%cd%"
call 7z x vrl-app-for-github.zip -y

xcopy /y C:\projects\vrl-neurobox-plugin\lib\VRL\VRL\dist\VRL.jar ugInit-consolApp\.application\lib\
xcopy /y C:\projects\vrl-neurobox-plugin\lib\VRL-UG\VRL-UG\dist\VRL-UG.jar ugInit-consolApp\.application\property-folder\plugin-updates\
xcopy /y C:\projects\vrl-neurobox-plugin\lib\VRL-UG\VRL-UG\dist\VRL-UG.jar ugInit-consolApp\.application\property-folder\plugins\

echo "%cd%
cd ugInit-consolApp
del .application\property-folder\plugins\unzipped\VRL-UG.jar
del .application\property-folder\plugins\unzipped\VRL-UG.xml
del .application\property-folder\plugins\unzipped\VRL-UG-API.jar
del .application\property-folder\plugins\unzipped\VRL-UG-API.xml
del .application\property-folder\plugins\VRL-UG.jar
del .application\property-folder\plugins\VRL-UG.xml
del .application\property-folder\plugins\VRL-UG-API.jar
del .application\property-folder\plugins\VRL-UG-API.xml
del .application\property-folder\plugins\unzipped\VRL-UG.jar
del .application\property-folder\plugins\unzipped\VRL-UG.xml
del .application\property-folder\plugins\unzipped\VRL-UG-API.jar
del .application\property-folder\plugins\unzipped\VRL-UG-API.xml
del /q .application\property-folder\plugins\unzipped\*
del /q .application\property-folder\property-folder\*
echo "%cd%"
set BASEPATH=C:\projects\vrl-neurobox-plugin\lib\VRL-UG\VRL-UG\jars
xcopy %BASEPATH%\apache-xmlrpc-3.1.3\lib\commons-logging-1.1.jar .application\lib\
xcopy %BASEPATH%\apache-xmlrpc-3.1.3\lib\ws-commons-util-1.0.2.jar .application\lib
xcopy %BASEPATH%\apache-xmlrpc-3.1.3\lib\xmlrpc-client-3.1.3.jar .application\lib
xcopy %BASEPATH%\apache-xmlrpc-3.1.3\lib\xmlrpc-common-3.1.3.jar .application\lib
xcopy %BASEPATH%\apache-xmlrpc-3.1.3\lib\xmlrpc-server-3.1.3.jar .application\lib

call C:\projects\vrl-neurobox-plugin\run.bat
call C:\projects\vrl-neurobox-plugin\run.bat
call C:\projects\vrl-neurobox-plugin\run.bat

cd C:\projects\vrl-neurobox-plugin;
md final-jars
cd final-jars
xcopy C:\projects\vrl-neurobox-plugin\lib\VRL-UG\VRL-UG\dist\VRL-UG.jar .
xcopy C:\projects\vrl-neurobox-plugin\lib\ugInit-consolApp\.application\property-folder\plugins\VRL-UG-API.jar .
xcopy C:\projects\vrl-neurobox-plugin\lib\VRL\VRL\dist\VRL.jar .
xcopy %BASEPATH%\apache-xmlrpc-3.1.3\lib\commons-logging-1.1.jar .
xcopy %BASEPATH%\apache-xmlrpc-3.1.3\lib\ws-commons-util-1.0.2.jar .
xcopy %BASEPATH%\apache-xmlrpc-3.1.3\lib\xmlrpc-client-3.1.3.jar .
xcopy %BASEPATH%\apache-xmlrpc-3.1.3\lib\xmlrpc-common-3.1.3.jar .
xcopy %BASEPATH%\apache-xmlrpc-3.1.3\lib\xmlrpc-server-3.1.3.jar .
