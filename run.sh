#!/bin/bash
set -e

JAVA_HOME="$(/usr/libexec/java_home -v 21 2>/dev/null)" || {
	echo "Java 21 nao encontrado. Instale um JDK 21 para executar o projeto." >&2
	exit 1
}
export JAVA_HOME
export PATH="$JAVA_HOME/bin:$PATH"

echo "Usando Java: $(java -version 2>&1 | head -1)"
mvn spring-boot:run "$@"

##comando para dar permissão de execução no arquivo run.sh
##chmod +x run.sh