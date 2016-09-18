git clone https://github.com/stephanmg/VRL-Neurolucida-Project
cd VRL-Neurolucida-Project
mkdir lib\
xcopy C:\projects\vrl-neurobox-plugin\lib\VRL\VRL\dist\VRL.jar lib\
xcopy C:\projects\vrl-neurobox-plugin\lib\VRL-UG\VRL-UG\dist\VRL-UG.jar lib\
xcopy C:\projects\vrl-neurobox-plugin\lib\ugInit-consolApp\.application\property-folder\plugins\VRL-UG-API.jar lib\
set BASEPATH=C:\projects\vrl-neurobox-plugin\lib\VRL-UG\VRL-UG\jars
xcopy %BASEPATH%\apache-xmlrpc-3.1.3\lib\commons-logging-1.1.jar .application\lib\
xcopy %BASEPATH%\apache-xmlrpc-3.1.3\lib\ws-commons-util-1.0.2.jar .application\lib
xcopy %BASEPATH%\apache-xmlrpc-3.1.3\lib\xmlrpc-client-3.1.3.jar .application\lib
xcopy %BASEPATH%\apache-xmlrpc-3.1.3\lib\xmlrpc-common-3.1.3.jar .application\lib
xcopy %BASEPATH%\apache-xmlrpc-3.1.3\lib\xmlrpc-server-3.1.3.jar .application\lib
echo "NEUROLUCID PROJECT PATH"
echo "%cd%

echo "TODO Build with gradle"

echo "TODO artifacts to be deployed from jar folder?"
