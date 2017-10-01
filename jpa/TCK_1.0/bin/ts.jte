##
## Copyright 2006 Sun Microsystems, Inc. All rights reserved.
## SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
##
##
## @(#)ts.jte	1.7 06/04/20
##
#########################################################################
##
##   JavaTest Environment file for Java Persistence API Standalone Test Suite
##
##   Environment specific properties in this file will likely
##   have to be modified prior to running the Java Persistence API TCK. 
##   Instructions for modifying these properties are contained in this
##   file.
##
##   This file is processed by an external tool that helps generate the
##   TCK documents.  Therefore this file has a standard format that must
##   be followed.  This file is a standard Java Properties file with
##   very specific comment formatting.  Users can write property specific
##   comments by using the property name and an ampersand (@).  As an
##   example the following comment applies to the foo.bar property:
##       # @foo.bar  - This is a comment pertaining to foo.bar
##       #             that spans multiple lines.
##   This comment must be preceded by a single hash (#) charater and
##   the property name must be prepended with an ampersand (@).  The
##   comment can appear anywhere in the ts.jte file.  If users have
##   comments that belong in ts.jte but DO NOT pertain to a particular
##   property the user must start the comment with at least 2 hash (#)
##   characters.  The following is a valid non-property comment:
##     ## A valid non-property comment
##     ## that spans multiple lines.
#########################################################################

########################################################################
## Javatest batch mode work directory and report directory, and policy for 
## handling existing work and report directories.  These properties affects
## runclient and report targets, but not gui target.
## To disable generating test report, unset report.dir, or set it to "none" 
## either here or from command line, as in the following command:
##               tsant runclient -Dreport.dir="none"
##
# @if.existing.work.report.dirs specifies how existing work.dir and
# report.dir will be handled, and it must be one of the following values:
# overwrite     overwrites all content in work.dir and report.dir
# backup        moves all content in work.dir and report.dir to
#               work.dir_time_day_bak and report.dir_time_day_bak, 
#               respectively 
# append        reuses and preserves the existing work.dir and report.dir
# auto          lets the build files decide which mode to use
#               (overwrite, backup or append).  the value is determined
#               like this:
#                  if.existing.work.report.dirs == auto
#                    if in TCK workspace
#                      if.existing.work.report.dirs = overwrite
#                    else we are in a distribution bundle
#                      if.existing.work.report.dirs = append
#                    end if
#                  else
#                    if.existing.work.report.dirs = value in this file
#                  end if
######################################################################## 
work.dir=/tmp/JTwork
report.dir=/tmp/JTreport
if.existing.work.report.dirs=auto

########################################################################
# @javatest.timeout.factor This property specifies the scale factor used by
# Javatest to adjust the time JavaTest will wait for a given test to
# complete before returning failure.  For instance if the default test timeout
# is 5 minutes, this value will be multiplied by 5 minutes to determine
# the total timeout delay.  Note: this value only works with Javatest's
# batch mode (runclient).  When using the Javatest GUI users must change
# this timeout factor in the GUI. Configure -> Edit Configuration -> View
# -> choose Standard Values -> select tab Execution ->  set Time Factor. 
########################################################################
javatest.timeout.factor=1.0

## Settings for Vendor Java Persistence API Implementation
jpa.home=${ts.home}
jpa.host=127.0.0.1

###############################################################
## When installing TCK/RI on Windows, users must install the TCK and
## the RI on the same drive.  Also note that you should never
## specify drive letters in any path defined in this properties
## file.
###############################################################
## Users must set this property when running on Windows.  The
## appropriate value on Windows is a semi-colon (;).  If you are
## not running on Windows leave this property set to its default
## value of colon (:) for other .
###############################################################
pathsep=:

###############################################################
## The directory separator for the platform.  User should not change
## this property.
###############################################################
dirsep=/

###############################################################
# @ts.display -- location to display TCK output on Unix
###############################################################
ts.display=:0.0

###############################################################
# @tz - your local timezone. For valid values, consult your
#       Operating System documentation.
###############################################################
tz=GMT

###############################################################
## The following property defines a file which contains
#  name/value pairs of properties intended to be passed to the
#  Persistence Provider during the creation of the
#  EntityManagerFactory. These properties correspond
#  to the persistence.xml properties and may contain
#  provider-specific properties including
#  database connection properties.
#
# This default properties file contains can be used to run with the RI.
###############################################################
persistence.unit.properties.file.full.path=${ts.home}/bin/datanucleus-provider.properties

###############################################################
## Configure the behavior of whether tables will be created when
## tsant init.database is invoked.
#
# NOTE: The Java Persistence API 1.0 Specification permits
# DDL generation to be supported by an implementation but
# it is not required.
# 
# @create.cmp.tables
#	-  When set to false, the persistence provider
# 	   is responsible for creating tables
#
# 	-  When set to true, init.datbaseName will
# 	   create the tables used by the persistence provider
#
###############################################################
create.cmp.tables=true

###############################################################
#
# The sql for the tables are contained in:
#
#  $TS_HOME/[databaseName]/sql/[databaseName].ddl.persistence.sql
#
# @databaseName
# 	Defines which database will be used for certification.
#	This property will be used to determine the corresponding sql
#       to intialize and can be one of the following:
#
# 	- derby
#	- mysql
#	- pointbase
#	- sybase
#	- db2
#	- mssqlserver
#	- oracle
#	- postgresql
#
# If using a database other than above, you need to create
# your own DDL files but can use these files for reference of what
# tables are required.
#
###############################################################
#databaseName=mariadb
databaseName=postgresql

###############################################################
# @jdbc.lib.class.path - This property is used by the
#                        database.classes properties to point to
#                        where the JDBC drivers live.  
###############################################################
jdbc.lib.class.path=${jpa.home}/jdbc/lib
#
###############################################################
##
## Info to be Used for DataBase Initialization
##
###############################################################
database.dbName=jpatck
database.server=${jpa.host}
database.port=

#database.url=jdbc:mariadb://${database.server}/${database.dbName}?useServerPrepStmts=false
#database.driver=org.mariadb.jdbc.Driver
#database.classes=${jdbc.lib.class.path}/mariadb-java-client.jar
#database.dataSource=org.mariadb.jdbc.Driver
#database.user=jpa
#database.passwd=

database.url=jdbc:postgresql:${database.dbName}
database.driver=org.postgresql.Driver
database.classes=${jdbc.lib.class.path}/postgresql-jdbc.jar
database.dataSource=org.postgresql.Driver
database.user=postgres
database.passwd=

database.properties=DatabaseName\=\"${database.dbName}\":user\=${database.user}:password\=${database.passwd}:serverName\=${database.server}:portNumber=${database.port}
#database.dbName=derbyDB
#database.server=${jpa.host}
#database.port=1527
#database.user=cts1
#database.passwd=cts1
#database.url=jdbc:derby://${database.server}:${database.port}/${database.dbName};create=true
#database.driver=org.apache.derby.jdbc.ClientDriver
#database.classes=${jdbc.lib.class.path}/derbyclient.jar
#database.dataSource=org.apache.derby.jdbc.ClientDataSource
#database.properties=DatabaseName\=\"${database.dbName}\":user\=${database.user}:password\=${database.passwd}:serverName\=${database.server}:portNumber=${database.port}

###############################################################
## Schema locations for Persistence xml files.
## Used for xml validation when building tests.
###############################################################
alt.schema.dir=${ts.home}/lib/schemas

###############################################################
## Classpath properties required by the JPA TCK:
# @jpa.classes          --  Classes required by RI Persistence Provider
# @ts.run.classpath 	--  Classes needed for test run
# @ts.harness.classpath --  Classes required by javatest
# @ts.classpath         --  Classes used to build the Persistence tests
# @ts.lib.classpath     --  Classes used to build cts.jar 
###############################################################

jpa.classes=${jpa.home}/lib/persistence-api.jar:${jpa.home}/datanucleus/datanucleus-core.jar:${jpa.home}/datanucleus/datanucleus-rdbms.jar:${jpa.home}/datanucleus/datanucleus-api-jpa.jar:${jpa.home}/datanucleus/log4j.jar:${jpa.home}/datanucleus/classes
#jpa.classes=${jpa.home}/lib/toplink-essentials.jar:${jpa.home}/lib/toplink-essentials-agent.jar

ts.run.classpath=${ts.home}/classes:${database.classes}:${jpa.classes}
#ts.run.classpath=${ts.home}/classes:${database.classes}

ts.harness.classpath=${ts.home}/lib/tsharness.jar:${ts.home}/lib/sigtest.jar:${ts.run.classpath}:${ts.home}/lib/javatest.jar:${ts.home}/tools/ant/lib/ant.jar

#classpath used for building CTS tests only (DO NOT MODIFY)
ts.classpath=${ts.harness.classpath}:${jpa.classes}

########################################################################
## Common environment for both ts_unix and ts_win32
#
# @command.testExecute - This command is used to execute any test 
#                        clients which are not run inside an
#                        application client container.  For example,
#                        any URL clients or standalone java clients
#                        would be executed with this command.  Some
#                        test directories which make use of this command
#                        are servlet and jsp.
########################################################################
command.testExecute=com.sun.ts.lib.harness.ExecTSTestCmd \
        CLASSPATH=${ts.run.classpath}:${ts.harness.classpath}:\
                  ${JAVA_HOME}/../lib/tools.jar:\
        DISPLAY=${ts.display} \
        HOME="${user.home}" \
        windir=${windir} \
        SYSTEMROOT=${SYSTEMROOT} \
        ${JAVA_HOME}/bin/java \
        -Dcts.tmp=$harness.temp.directory \
        -Djava.security.policy=${ts.home}/bin/harness.policy \
        -Ddeliverable.class=${deliverable.class} $testExecuteClass $testExecuteArgs
#       -Ddeliverable.class=${deliverable.class} -javaagent:${jpa.home}/lib/toplink-essentials-agent.jar $testExecuteClass $testExecuteArgs

#########################################################################
## Environment for ts_unix
## test execution command inherit from common environment
## defined above:  testExecute.
## If you need to override them, uncomment them in the
## following section.
#########################################################################
env.ts_unix.menu=true
##env.ts_unix.command.testExecute=

########################################################################
## Environment for ts_win32
## test execution commands inherit from common environment
## defined above: testExecute.
## If you need to override them, uncomment them in the
## following section.
########################################################################
env.ts_win32.menu=true
##env.ts_win32.command.testExecute=

#########################################################################
# @sigTestClasspath: This property must be set when running signature
#                    tests.  This property should be set to a list of
#                    jar files and/or directories which contain your
#                    Java Persistence and Java SE classes. Paths must be
#                    separated by the appropriate path separator
#                    (';' windows, ':' Unixes).
#########################################################################
sigTestClasspath=${jpa.classes}:${JAVA_HOME}/lib/rt.jar
#sigTestClasspath=${jpa.home}/lib/toplink-essentials.jar:${JAVA_HOME}/lib/rt.jar

########################################################################
## These properties are used by the harness.  "harness.log.port"
## specifies the port that server components use to send logging
## output back to JavaTest.  If the default port # is not available
## on the machine running JavaTest, then you can set it here.
##
## "harness.log.traceflag" is used to turn on/off verbose debugging output
##  for the tests.
##
## "harness.executeMode" is used to run the harness in the following modes
##  of execution:
##    0 - default (deploy, run, undeploy)
##    1 - deploy only
##    2 - run only
##    3 - undeploy only
##    4 - deploy and run only
##
##
##  @harness.socket.retry.count - denotes the number of time we should
##    attempt to create a server socket when intilizing a test
##    client.  The socket is used for logging purposes.
##
########################################################################
harness.temp.directory=${ts.home}/tmp
harness.log.port=2000
harness.log.traceflag=false
harness.executeMode=0
harness.log.delayseconds=1
harness.socket.retry.count=10

###############################################################
## These properties must be set to tell the Test harness the
## class names of your porting class implementations.
#
# @porting.ts.url.class.1  VI of
#    com.sun.ts.lib.porting.TSURLInterface
###############################################################
porting.ts.url.class.1=com.sun.ts.lib.implementation.sun.common.SunRIURL

###################################################################
###################################################################
###################################################################
##  PROPERTIES USERS WILL NOT HAVE TO SET ARE BELOW
###################################################################
###################################################################
###################################################################

##build level
##1: compile only
##2: compile and build component archives (e.g., jar's, war's)
##3: compile and build component and application archives
##default is set to 3
build.level=2

###############################################################
## JPA Deliverable Class
## DO NOT CHANGE THIS PROPERTY
############################################################### 
deliverable.class=com.sun.ts.lib.deliverable.jpa.JPADeliverable

#######################################################################
## platform.mode is used to tell the enviroment we are in standalone
## mode
## DO NOT CHANGE THIS PROPERTY
######################################################################
platform.mode=standalone

#######################################################################
## the name for the persistence unit used in this TCK. Its value is the
## same as the name attribute in persistence.xml
## DO NOT CHANGE THIS PROPERTY
######################################################################
persistence.unit.name=JPATCK

#######################################################################
## Location of ts_home
## DO NOT CHANGE THIS PROPERTY
######################################################################

ts_home=${ts.home}
