#!/bin/sh

#set JAVA_HOME
#JAVA_HOME=/usr/alibaba/java

#check JAVA_HOME & java
noJavaHome=false
if [ -z "$JAVA_HOME" ] ; then
    noJavaHome=true
fi
if [ ! -e "$JAVA_HOME/bin/java" ] ; then
    noJavaHome=true
fi
if $noJavaHome ; then
    echo
    echo "Error: JAVA_HOME environment variable is not set."
    echo
    exit 1
fi
#==============================================================================

#stop Server
$JAVA_HOME/bin/jps |grep Main|awk -F ' ' '{print $1}'|while read line
do
  eval "kill -9 $line"
done
#==============================================================================

#sleep sometime
sleep 1

#set DDA_HOME
CURR_DIR=`pwd`
cd `dirname "$0"`/..
DDA_HOME=`pwd`
cd $CURR_DIR
if [ -z "$DDA_HOME" ] ; then
    echo
    echo "Error: DDA_HOME environment variable is not defined correctly."
    echo
    exit 1
fi
#==============================================================================

#startup Server
. $DDA_HOME/bin/startup.sh
#==============================================================================