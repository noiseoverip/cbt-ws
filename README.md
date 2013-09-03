cbt-ws
======

PROJECT SETUP

1. Fix unresolved Maven dependencies
Library for parsing DEX files (https://code.google.com/p/smali/) is not available in MAVEN CENTRAL, copy is included in /lib folder
Add lib\baksmali-1.4.2.jar to local maven repository with:
mvn install:install-file -Dfile=lib\baksmali-1.4.2.jar -DgroupId=org.jf.dexlib -DartifactId=baksmali -Dversion=1.4.2 -Dpackaging=jar

2. Add the following properties into Maven settings.xml

		<profile>
			<id>inject-db-properties</id>
			<properties>
				<!-- Name of database to be used to testing-->
				<cbt.dbtest.name>cbttest</cbt.dbtest.name>
				<!-- Name of database to be used for production-->				
				<cbt.dbtest.url>jdbc:mysql://127.0.0.1:3306/cbttest</cbt.dbtest.url>
				<!-- JDBC url to EXISTING database in same MySQL instance as testing database -->
				<cbt.dbbootstrap.url>jdbc:mysql://127.0.0.1:3306/bootstrap</cbt.dbbootstrap.url>
				<cbt.dbtest.user>root</cbt.dbtest.user>
				<cbt.dbtest.password></cbt.dbtest.password>
				<!-- JDBC url to production database -->
				<cbt.db.url>jdbc:mysql://127.0.0.1:3306/cbt</cbt.db.url>
				<cbt.db.name>cbt</cbt.db.name>
				<cbt.db.user>root</cbt.db.user>
				<cbt.db.password></cbt.db.password>
			</properties>
		</profile>

3. Run integration tests (this is needed to compile the project since it generated JOOQ classes)
maven verify -P prepareForTest,jooqgenerate
This will:
 - Create database
 - Populate it with test data
 - Re-generate JOOQ libraries
 - Run integration tests (*IT.java)
 - Delete database

4. Setup production database
 1. Create database where name=${cbt.db.name} (from Maven settings.xml)
 2. Run: maven initialize -P setupProductionDbSchema

5. One more nasty thing to do is:
Go to /src/main/webapp/js/cbtclient.js and modify variable rootURL according to where you have deployed the web app

API used by Client application (deprecated)
/user?name
/checkout/testpackage
/checkout/testpackage.zip
/device
/device/type
/device/{id}
/devicejob/waiting
/devicejob/{id}/result

### Testing procedure:
execute: maven tests

1. drops existing CBT database
2. creates database named "cbttest" from src/main/resources/cbt.sql
3. generate JOOQ classes using created database
4. executes tests
5. removes database

### Using RIP
For authentication, need to add the following headers:

* username - provide your user name
* password - provide MD% of your password

Testing user accounts:

* name: testuser1 
* password:41da76f0fc3ec62a6939e634bfb6a342 (MD5 of testuser1)



