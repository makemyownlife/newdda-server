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
JAVA_OPTS="-server -Xms1024m -Xmx1024m -Xmn256m -Xss256k"
#performance Options
JAVA_OPTS="$JAVA_OPTS -XX:+AggressiveOpts"
JAVA_OPTS="$JAVA_OPTS -XX:+UseBiasedLocking"
JAVA_OPTS="$JAVA_OPTS -XX:+UseFastAccessorMethods"
JAVA_OPTS="$JAVA_OPTS -XX:+DisableExplicitGC"
JAVA_OPTS="$JAVA_OPTS -XX:+UseParNewGC"
JAVA_OPTS="$JAVA_OPTS -XX:+UseConcMarkSweepGC"
JAVA_OPTS="$JAVA_OPTS -XX:+CMSParallelRemarkEnabled"
JAVA_OPTS="$JAVA_OPTS -XX:+UseCMSCompactAtFullCollection"
JAVA_OPTS="$JAVA_OPTS -XX:+UseCMSInitiatingOccupancyOnly"
JAVA_OPTS="$JAVA_OPTS -XX:CMSInitiatingOccupancyFraction=75"
#GC Log Options
#JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCApplicationStoppedTime"
#JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCTimeStamps"
#JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDetails"
#debug Options
#JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,address=8065,server=y,suspend=n"
#==============================================================================

#set HOME
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

#set CLASSPATH
DDA_CLASSPATH="$DDA_HOME/conf:$DDA_HOME/lib/classes"
for i in "$DDA_HOME"/lib/*.jar
do
    DDA_CLASSPATH="$DDA_CLASSPATH:$i"
done
#==============================================================================

#startup Server
RUN_CMD="\"$JAVA_HOME/bin/java\""
RUN_CMD="$RUN_CMD -Ddda.home=\"$DDA_HOME\""
RUN_CMD="$RUN_CMD -classpath \"$DDA_CLASSPATH\""
RUN_CMD="$RUN_CMD $JAVA_OPTS"
RUN_CMD="$RUN_CMD com.elong.pb.newdda.DdaMain $@"
RUN_CMD="$RUN_CMD >> \"$DDA_HOME/logs/console.log\" 2>&1 &"
echo $RUN_CMD
eval $RUN_CMD
#==============================================================================
