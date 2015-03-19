#!/bin/bash


folder="$(dirname "$(readlink -f $0)")"
cd "${folder}/target"

findJar(){
	echo "$(find $1 -maxdepth 1 -name "${2:-*}.jar")"
}


jarFile="$(findJar .)"

javaCmd="${JAVA_HOME}/bin/java"

cat > client.policy <<EOF
grant {
    permission java.security.AllPermission;
};
EOF

$javaCmd \
	-Djava.security.policy=client.policy \
	-jar ${jarFile}

cd - >/dev/null
