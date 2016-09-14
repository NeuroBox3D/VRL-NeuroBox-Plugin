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
echo "DEBUGGED UNTIL HERE"
cd ugInit-consolApp\
echo "BEFORE RUNNING APP FIRST TIME"
xcopy C:\projects\vrl-neurobox-plugin\lib\VRL\VRL\dist\VRL.jar ugInit-consolApp\.application\lib\
echo "DEBUG FROM HERE"

rmdir \home\travis\.vrl\0.4.2\default\plugins\VRL-UG* /q /s
rmdir .application\property-folder\plugins\unzipped\VRL-UG* /q /s
rmdir .application\property-folder\plugins\VRL-UG* /q /s
rmdir .application\property-folder\plugins\VRL-UG*.xml /q /s
rmdir .application\property-folder\plugins\VRL-UG*.jar /q /s
rmdir .application\property-folder\plugins\unzipped\VRL-UG*.jar /q /s
rmdir .application\property-folder\plugins\unzipped\VRL-UG*.xml /q /s
rmdir .application\property-folder\plugins\unzipped\ /q /s
rmdir .application\property-folder\property-folder\* /q /s

xcopy /home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL-UG/VRL-UG/dist/VRL-UG.jar .application/property-folder/plugin-updates/VRL-UG.jar
xcopy /home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL-UG/VRL-UG/dist/VRL-UG.jar .application/property-folder/plugins/VRL-UG.jar

# install vrl-ug plugin
call run.bat
# build vrl-ug-api
call run.bat
# test

BASEPATH=/home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL-UG/VRL-UG/jars

cp $BASEPATH/apache-xmlrpc-3.1.3/lib/commons-logging-1.1.jar .application/lib/
cp $BASEPATH/apache-xmlrpc-3.1.3/lib/ws-commons-util-1.0.2.jar .application/lib/
cp $BASEPATH/apache-xmlrpc-3.1.3/lib/xmlrpc-client-3.1.3.jar .application/lib
cp $BASEPATH/apache-xmlrpc-3.1.3/lib/xmlrpc-common-3.1.3.jar .application/lib
cp $BASEPATH/apache-xmlrpc-3.1.3/lib/xmlrpc-server-3.1.3.jar .application/lib

echo "TEST TEST";

CALL_PATH=$(pwd)
APPDIR="$(dirname "$0")/.application"
cd "$APPDIR"
APPDIR="$(pwd)"

PROPERTY_FOLDER="property-folder/"
PROJECT_FILE="project.jar"

CONF="-property-folder $PROPERTY_FOLDER -plugin-checksum-test no -install-plugin-help no -install-payload-plugins yes"

OS=$(uname -a)


if [ "$1" == "-help" ]
then

cat << EOF

** Help: **

Basic Usage:  

./run.sh                      Starts VRL-Project

EOF

exit 0

fi

LIBDIR32="lib/linux/x86:custom-lib/linux/x86"
LIBDIR64="lib/linux/x64:custom-lib/linux/x64"
LIBDIROSX="lib/osx:custom-lib/osx"

if [[ $OS == *x86_64* ]]
then
  echo ">> detected x86 (64 bit) os"
  LIBDIR="$LIBDIR64:$LIBDIROSX"
  JAVAEXE="jre/x64/bin/java"
elif [[ $OS == *86* ]]
then
  echo ">> detected x86 (32 bit) os"
  LIBDIR="$LIBDIR32:$LIBDIROSX"
  JAVAEXE="jre/x86/bin/java"
else
  echo ">> unsupported architecture!"
  echo " --> executing installed java version"
  JAVAEXE="java"
fi

if [ ! -e $JAVAEXE ]
then
  echo ">> integrated jre not found!"
  echo " --> executing installed java version"
  JAVAEXE="java"
fi

if [[ $OS == *Darwin* ]]
then
  # ugly hack to enable vtk on osx
  export DYLD_LIBRARY_PATH="$PROPERTY_FOLDER/plugins/VRL-VTK/natives/osx/:$DYLD_LIBRARY_PATH"
  # force java 6 on mac os
  JAVAEXE=/System/Library/Frameworks/JavaVM.framework/Versions/1.6/Home/bin/java
fi
  
# optimized for jre 7 (19.04.2012)
$JAVAEXE -Xms128m -Xmx4096m -XX:MaxPermSize=256m -Djava.library.path="$LIBDIR:/home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL-UG/VRL-UG/jars/apache-xmlrpc-3.1.3/lib/commons-logging-1.1.jar:/Users/markus/Developing/VRL/VRL/VRL/jars/groovy/groovy-all.jar:/home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL-UG/VRL-UG/jars/apache-xmlrpc-3.1.3/lib/ws-commons-util-1.0.2.jar:/home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL-UG/VRL-UG/jars/apache-xmlrpc-3.1.3/lib/xmlrpc-client-3.1.3.jar:/home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL-UG/VRL-UG/jars/apache-xmlrpc-3.1.3/lib/xmlrpc-common-3.1.3.jar:/home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL-UG/VRL-UG/jars/apache-xmlrpc-3.1.3/lib/xmlrpc-server-3.1.3.jar"  -jar "$PROJECT_FILE" $CONF

$JAVAEXE -Xms128m -Xmx4096m -XX:MaxPermSize=256m -Djava.library.path="$LIBDIR:/home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL-UG/VRL-UG/jars/apache-xmlrpc-3.1.3/lib/commons-logging-1.1.jar:/Users/markus/Developing/VRL/VRL/VRL/jars/groovy/groovy-all.jar:/home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL-UG/VRL-UG/jars/apache-xmlrpc-3.1.3/lib/ws-commons-util-1.0.2.jar:/home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL-UG/VRL-UG/jars/apache-xmlrpc-3.1.3/lib/xmlrpc-client-3.1.3.jar:/home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL-UG/VRL-UG/jars/apache-xmlrpc-3.1.3/lib/xmlrpc-common-3.1.3.jar:/home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL-UG/VRL-UG/jars/apache-xmlrpc-3.1.3/lib/xmlrpc-server-3.1.3.jar"  -jar "$PROJECT_FILE" $CONF

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
