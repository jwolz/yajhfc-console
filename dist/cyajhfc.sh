#!/bin/sh
# Starts yajhfc-console

DIR=`dirname $0`

YAJHFC="$DIR/yajhfc.jar"
JARTOEXEC="$DIR/yajhfc-console.jar"

JAVA=`which java`

if [ ! -f "$YAJHFC" ]; then
	echo "$YAJHFC not found!"
	exit 1 ;
fi

if [ ! -f "$JARTOEXEC" ]; then
	echo "$JARTOEXEC not found!"
	exit 1 ;
fi

if [ -z "$JAVA" ]; then
	echo "Java executable not found in path."
	exit 1 ;
fi


exec $JAVA -jar "$JARTOEXEC" "$@"

