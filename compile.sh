#!/bin/bash
DIR=/Users/antoine/Documents/Programs/
UIDIR=$DIR/gs-ui/
FILES=`find $UIDIR -iname "*.scala"`

CLASSPATH=$DIR/gs-core/gs-core-bin.jar:$DIR/gs-algo/gs-algo-bin.jar:$DIR/gs-core/lib/mbox2.jar:$DIR/gs-core/lib/pherd.jar:$CLASSPATH

fsc -classpath $CLASSPATH -d $UIDIR/bin $FILES
