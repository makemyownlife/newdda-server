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

#set JAVA_OPTS
JAVA_OPTS="-Xss256k"
#==============================================================================

#stop Server
$JAVA_HOME/bin/jps |grep DdaMain |awk -F ' ' '{print $1}'|while read line
do
  eval "kill -9 $line"
done
#==============================================================================

#set HOME
CURR_DIR=`pwd`
cd `dirname "$0"`/..
DDA_HOME=`pwd`
cd $DDA_DIR
if [ -z "$DDA_HOME" ] ; then
    echo
    echo "Error: DDA_HOME environment variable is not defined correctly."
    echo
    exit 1
fi
#==============================================================================

#set CLASSPATH
DDA_CLASSPATH="$DDA_HOME/conf:$DDA_HOME/lib/classes"
for i in "$DDA_HOME"/lib/*.jar
do
    DDA_CLASSPATH="$DDA_CLASSPATH:$i"
done
#==============================================================================

#shutdown Server
RUN_CMD="\"$JAVA_HOME/bin/java\""
RUN_CMD="$RUN_CMD -DDDA.home=\"$DDA_HOME\""
RUN_CMD="$RUN_CMD -classpath \"$DDA_CLASSPATH\""
RUN_CMD="$RUN_CMD $JAVA_OPTS"
RUN_CMD="$RUN_CMD com.alibaba.DDA.DDAShutdown $@"
RUN_CMD="$RUN_CMD >> \"$DDA_HOME/logs/console.log\" 2>&1 &"
echo $RUN_CMD
eval $RUN_CMD
#==============================================================================

