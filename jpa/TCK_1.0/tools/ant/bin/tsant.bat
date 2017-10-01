@echo off
REM
REM Copyright 2006 Sun Microsystems, Inc. All rights reserved.
REM SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
REM

REM
REM @(#)tsant.bat	1.31 06/04/28
REM

@setlocal

set CMD_LINE_ARGS=
set _SET_BUILDVI=false
set _SET_BUILDVI_VALUE=false
set _SET_KEYWORDS=false
set _SET_KEYWORDS_VALUE=all
set _SET_DIR_CMD=false
set _SET_DIR_CMD_VALUE=
set ANT_OPTS=

goto setupArgs

:setbuildvi
set _SET_BUILDVI=true
shift
if ""%1""==""true"" goto setbuildvi1
if ""%1""==""false"" goto setbuildvi1
goto usage
:setbuildvi1
set _SET_BUILDVI_VALUE=%1
shift
goto setupArgs

:setbuildviquotes
set _SET_BUILDVI=true
shift
set _SET_BUILDVI_VALUE=true
goto setupArgs


:setkeywords
set _SET_KEYWORDS=true
shift
if ""%1""==""all"" goto setkeywords1
if ""%1""==""forward"" goto setkeywords1
if ""%1""==""reverse"" goto setkeywords1
goto usage
:setkeywords1
set _SET_KEYWORDS_VALUE=%1
shift
goto setupArgs

:setdircmd
set _SET_DIR_CMD=true
set _SET_DIR_CMD_VALUE=%1
shift
goto setupArgs

rem Slurp the command line arguments. This loop allows for an unlimited number
rem of arguments (up to the command line limit, anyway).

:setupArgs
if ""%1""=="""" goto doneArgs
if ""%1""==""-Dbuild.vi"" goto setbuildvi
if ""%1""=="""-Dbuild.vi=true""" goto setbuildviquotes
if ""%1""==""-Dkeywords"" goto setkeywords
if ""%1""==""lc"" goto setdircmd
if ""%1""==""llc"" goto setdircmd
if ""%1""==""ld"" goto setdircmd
if ""%1""==""lld"" goto setdircmd
if ""%1""==""pc"" goto setdircmd
if ""%1""==""pd"" goto setdircmd

set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setupArgs

:doneArgs

:checkbuildvi
echo _SET_BUILDVI=%_SET_BUILDVI%
echo _SET_BUILDVI_VALUE=%_SET_BUILDVI_VALUE%
if %_SET_BUILDVI%==false goto checkkeywords
set CMD_LINE_ARGS=%CMD_LINE_ARGS% -Dbuild.vi=%_SET_BUILDVI_VALUE%
set ANT_OPTS=%ANT_OPTS% -Dbuild.vi=%_SET_BUILDVI_VALUE%

:checkkeywords
echo _SET_KEYWORDS=%_SET_KEYWORDS%
echo _SET_KEYWORDS_VALUE=%_SET_KEYWORDS_VALUE%
if %_SET_KEYWORDS%==false goto doneArgs2
set CMD_LINE_ARGS=%CMD_LINE_ARGS% -Dkeywords=%_SET_KEYWORDS_VALUE%

:doneArgs2

if %_SET_DIR_CMD%==true goto %_SET_DIR_CMD_VALUE%

goto start
:usage
echo ------------------
echo   ILLEGAL SYNTAX
echo ------------------
set CMD_LINE_ARGS=usage

:start
if "%TS_HOME%" == "" goto end1

if "%ANT_HOME%" == "" set ANT_HOME=%TS_HOME%\tools\ant
if not exist "%ANT_HOME%\bin\ant.bat" goto end3

if "%JAVA_HOME%" == "" goto end4

echo TS_HOME is set to %TS_HOME%
echo JAVA_HOME is set to %JAVA_HOME%
echo Using Ant %ANT_HOME%

REM CLASSPATH defines extra jar files used by ant.

REM unset JAVACMD. Use JAVA_HOME\bin\java instead
set JAVACMD=

REM prevent ant from using javac in the path. Use JAVA_HOME\bin\javac instead
set path=%JAVA_HOME%\bin;%path%

set TS_ANT_JAR=%TS_HOME%\lib\ant_sun.jar
set HARNESS_JARS=%TS_HOME%\lib\tsharness.jar
set JDOM_JARS=%TS_HOME%\lib\jdom.jar;%TS_HOME%\lib\saxpath.jar;%TS_HOME%\lib\jaxen-core.jar;%TS_HOME%\lib\jaxen-jdom.jar

REM
REM add jdk tools.jar to classpath
REM
set TOOLS_JAR=
if not exist %JAVA_HOME%\lib\tools.jar goto tools_jar1
    set TOOLS_JAR=%JAVA_HOME%\lib\tools.jar
    goto tools_jar_end
:tools_jar1
if not exist %JAVA_HOME%\..\lib\tools.jar goto tools_jar_end
    set TOOLS_JAR=%JAVA_HOME%\..\lib\tools.jar
:tools_jar_end

set CLASSPATH=%TS_ANT_JAR%;%HARNESS_JARS%;%JDOM_JARS%;%TOOLS_JAR%;%CLASSPATH%
set ANT_OPTS=%ANT_OPTS% -Xmx512M -Dwin.windir=%windir% -Dwin.systemroot=%SystemRoot%

if not exist build.xml goto skip1
call "%ANT_HOME%\bin\ant.bat" -emacs -listener com.sun.ant.TSBuildListener -logger com.sun.ant.TSLogger %CMD_LINE_ARGS%
goto end

:skip1
call "%ANT_HOME%\bin\ant.bat" -emacs -buildfile %TS_HOME%\bin\build.xml -listener com.sun.ant.TSBuildListener -logger com.sun.ant.TSLogger %CMD_LINE_ARGS%
goto end


REM subs to handle directory-related routines
REM

:lc
for /f "delims=" %%a in ('cd') do set srcdir=%%a
if %_SET_BUILDVI_VALUE%==false set newdir=%srcdir:src=classes%
if %_SET_BUILDVI_VALUE%==true set newdir=%srcdir:src=classes_vi_built%
dir %newdir% /P/B/O-D
goto end

:llc
for /f "delims=" %%a in ('cd') do set srcdir=%%a
if %_SET_BUILDVI_VALUE%==false set newdir=%srcdir:src=classes%
if %_SET_BUILDVI_VALUE%==true set newdir=%srcdir:src=classes_vi_built%
dir %newdir% /P/O-D
goto end

:pc
for /f "delims=" %%a in ('cd') do set srcdir=%%a
if %_SET_BUILDVI_VALUE%==false set newdir=%srcdir:src=classes%
if %_SET_BUILDVI_VALUE%==true set newdir=%srcdir:src=classes_vi_built%
start /d%newdir%
goto end

:ld
for /f "delims=" %%a in ('cd') do set srcdir=%%a
set newdir=%srcdir:src=dist%
dir %newdir% /P/B/O-D
goto end

:lld
for /f "delims=" %%a in ('cd') do set srcdir=%%a
set newdir=%srcdir:src=dist%
dir %newdir% /P/O-D
goto end

:pd
for /f "delims=" %%a in ('cd') do set srcdir=%%a
set newdir=%srcdir:src=dist%
start /d%newdir% cmd
goto end

:end1
    echo ERROR: TS_HOME is NOT SET!!
    echo Please set TS_HOME (ie: c:\files\ts ) before running tsant.
    echo Setup is INCOMPLETE - Exiting
    goto end
:end3
    echo ERROR: ANT_HOME is NOT a valid directory
    echo Please set ANT_HOME (ie: c:\files\ts\tools\ant ) to a valid directory
    echo before running tsant.
    echo Setup is INCOMPLETE - Exiting
    goto end
:end4
    echo ERROR: JAVA_HOME is NOT SET!!
    echo Please set JAVA_HOME (ie: c:\files\javase ) before running tsant.
    echo Setup is INCOMPLETE - Exiting
    goto end


:end
@endlocal
