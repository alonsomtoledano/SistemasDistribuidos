cd ./src/cameraRing/corbaMatriculasDetector
start orbd -ORBInitialPort 1050
java MatriculasServer -ORBInitialPort 1050 -ORBInitialHost localhost&