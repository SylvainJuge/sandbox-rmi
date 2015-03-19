#!/bin/bash

folder="$(dirname "$(readlink -f $0)")"
currentFolder=$(pwd)

cd "${folder}/target"

SERVER_PID=server.pid
REGISTRY_PID=registry.pid

findJar(){
	echo "$(find $1 -maxdepth 1 -name "${2:-*}.jar")"
}

doStop(){
	kill $(cat $SERVER_PID); rm $SERVER_PID
	kill $(cat $REGISTRY_PID); rm $REGISTRY_PID
}
trap doStop SIGTERM EXIT

cat > server.policy <<EOF
grant {
    permission java.security.AllPermission;
};
EOF

jarFile="$(findJar .)"
javaCmd="${JAVA_HOME}/bin/java"
registryCmd="${JAVA_HOME}/bin/rmiregistry"

$registryCmd  -J-classpath -J"$(findJar rmiregistry-dependency)" &
# rmiregistry forks, we have to get PID using jps
echo $(jps -v|grep RegistryImpl|awk '{print $1}')>$REGISTRY_PID

$javaCmd \
	-Djava.security.policy=server.policy \
	-jar ${jarFile} \
	"$*" &
echo $! > $SERVER_PID

wait $(cat $SERVER_PID)
cd $currentFolder
