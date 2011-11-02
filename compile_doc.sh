#!/bin/sh

if test $# -ne 1
then
	echo "Usage: $0 <sources-directory>"
else
    GSPATH=/home/antoine/Documents/Programs/
	CLASSPATH=$GSPATH/gs-core/gs-core-bin.jar:$GSPATH/gs-core/lib/pherd.jar:$GSPATH/gs-core/lib/mbox2.jar:$GSPATH/gs-algo/gs-algo-bin.jar
	MAINPATH=$1
	FILES=`find $MAINPATH -iname "*.scala"`
	scaladoc -classpath "$CLASSPATH" -d doc $FILES
fi
