# JPA TCK

This is not freely available, only available if you apply in writing to Oracle and sign a NDA and then never get a reply.


### Configure the TCK for use with DataNucleus

To get it (JPA1 TCK) to run with DataNucleus, the following can be used as a guide.

Edit _bin/xml/ts.import.xml_ to include

    <target name="compile">
        <ts.javac/>
        <antcall target="enhance" />
    </target>

    <!-- libs necessary to build the module -->
    <path id="enhancer.classpath">
        <fileset dir="${ts.home}/datanucleus">
            <include name="**/*.jar"/>
        </fileset>
        <pathelement path="${ts.classpath}" />
    </path>

    <target name="enhance" description="Class enhancement">
        <taskdef name="datanucleusenhancer" classpathref="enhancer.classpath"   classname="org.datanucleus.enhancer.EnhancerTask" />
        <datanucleusenhancer classpathref="enhancer.classpath" 
            dir="${class.dir}" failonerror="false" verbose="true" api="JPA">
            <fileset dir="${ts.home}">
                <include name="classes/**/*.class"/>
                <include name="src/${pkg.dir}/orm.xml"/>
            </fileset>
        </datanucleusenhancer>
    </target>

 

Add a file _bin/datanucleus-provider.properties_ that includes

    javax.persistence.provider=org.datanucleus.api.jpa.PersistenceProviderImpl
    datanucleus.ConnectionDriverName=com.mysql.jdbc.Driver
    datanucleus.ConnectionURL=jdbc:mysql://127.0.0.1/jpatck?useServerPrepStmts=false
    datanucleus.ConnectionUserName=jpa
    datanucleus.ConnectionPassword=


Use strict JPA compliance settings, turning off convenience helpers, so add

    datanucleus.findObject.typeConversion=false


Edit the file _bin/ts.jte_ to include (these are for MySQL so update to your DB)

    jpa.home={your_jpa_tck_area}
    jpa.host=127.0.0.1
    persistence.unit.properties.file.full.path=${ts.home}/bin/datanucleus-provider.properties
    create.cmp.tables=true
    databaseName=mysqldatabaseName=mysql
    jdbc.lib.class.path=${jpa.home}/jdbc/lib
    database.dbName=jpatck
    database.server=${jpa.host}
    database.port=
    database.user=jpa
    database.passwd=
    database.url=jdbc:mysql://${database.server}/${database.dbName}?useServerPrepStmts=false
    database.driver=com.mysql.jdbc.Driver
    database.classes=${jdbc.lib.class.path}/mysql-connector-java-5.0.5.jar
    database.dataSource=com.mysql.jdbc.Driver
    database.properties=DatabaseName\=\"${database.dbName}\":user\=${database.user}:password\=${database.passwd}:serverName\=${database.server}:portNumber=${database.port}
    
    jpa.classes=${jpa.home}/lib/persistence-api.jar:${jpa.home}/datanucleus/datanucleus-core.jar:${jpa.home}/datanucleus/datanucleus-rdbms.jar:${jpa.home}/datanucleus/datanucleus-api-jpa.jar:${jpa.home}/datanucleus/log4j.jar:${jpa.home}/datanucleus/classes
    
    ts.run.classpath=${ts.home}/classes:${database.classes}:${jpa.classes}

 
Create _jdbc/lib_ and put your JDBC driver jar in there.
Create _datanucleus/_ and put your DN jars, log4j.jar and log4j.properties in there.
Edit bin/ts.jte if necessary to make sure the DataNucleus jars in _datanucleus_ are those
listed.


### Setup on Linux

    export TS_HOME="{location_of_jpa_tck}"
    PATH=$PATH:${TS_HOME}/bin
    export ANT_HOME=$TS_HOME/tools/ant


### To setup the database

    cd $TS_HOME/bin
    tsant -f initdb.xml init.database

 
### To build the tck (and enhance the classes)

This needs building with Java 8 (since that is what JPA 1 had), so edit ~/.bashrc to have JAVA_HOME set to the Java 8 JRE.
Also set the datanucleus jars to be v5.2.0-release (built with Java 8) to fool it into building.

    setenv ANT_HOME $TS_HOME/tools/ant

    cd $TS_HOME/src
    tsant clean
    tsant build
    tsant tslib

 
### To run the tck

    cd $TS_HOME\bin
    tsant gui

 

 
