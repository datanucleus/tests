Build it
========
export ANT_HOME=$TS_HOME/tools/ant

cd $TS_HOME/src
tsant clean
tsant build
tsant tslib


Database
========
cd $TS_HOME/bin
tsant -f initdb.xml init.database


Run the TCK via GUI
===================
cd $TS_HOME/bin
tsant gui
