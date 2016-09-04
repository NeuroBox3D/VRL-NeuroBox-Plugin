#!/bin/bash

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
$JAVAEXE -cp  "/home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL-UG/VRL-UG/jars/apache-xmlrpc-3.1.3/lib/commons-logging-1.1.jar:/Users/markus/Developing/VRL/VRL/VRL/jars/groovy/groovy-all.jar:/home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL-UG/VRL-UG/jars/apache-xmlrpc-3.1.3/lib/ws-commons-util-1.0.2.jar:/home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL-UG/VRL-UG/jars/apache-xmlrpc-3.1.3/lib/xmlrpc-client-3.1.3.jar:/home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL-UG/VRL-UG/jars/apache-xmlrpc-3.1.3/lib/xmlrpc-common-3.1.3.jar:/home/travis/build/NeuroBox3D/VRL-NeuroBox-Plugin/lib/VRL-UG/VRL-UG/jars/apache-xmlrpc-3.1.3/lib/xmlrpc-server-3.1.3.jar" -Xms128m -Xmx4096m -XX:MaxPermSize=256m -Djava.library.path="$LIBDIR" -jar "$PROJECT_FILE" $CONF
