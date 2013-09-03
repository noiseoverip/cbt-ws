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
1. Create database
2. Populate it with test data
3. Re-generate JOOQ libraries
3. Run integration tests (*IT.java)
4. Delete database

4. Setup production database
4.1 Create database where name=${cbt.db.name} (from Maven settings.xml)
4.2 Run: maven initialize -P setupProductionDbSchema

5. One more nasty thing to do is:
Go to /src/main/webapp/js/cbtclient.js and modify variable rootURL according to where you have deployed the web app

API used by Client application
/user?name
/checkout/testpackage
/checkout/testpackage.zip
/device
/device/type
/device/{id}
/devicejob/waiting
/devicejob/{id}/result


API used in WEB GUI by page:
device.html
	GET /device?userId={userId}
deviceShare.html
	GET /device/{id}/share
	GET /user/all
login.html
	GET /user + JSON
register.html
	PUT /public/user + JSON
statistics.html
	GET /user/{userId}/stats/hosted
	GET /user/{userId}/stats/used
testconfig_new.html
	GET /user/{userId}/testprofile
	GET /testscript
	GET /testtarget
	PUT /testconfig + JSON
testconfigs.html
	GET /user/{userId}/testconfig
	PUT /testrun
testprofile_new.html
	GET /public/device-types
	PUT /user/{userId}/testprofile
testprofiles.html
	GET /user/{userId}/testprofile
	PUT /testrun
testrun_results.html
	GET /devicejob?testRunId={testRunId}
	GET devicejob/{deviceJobId}/result
testruns.html
	GET /user/{userId}/testrun
testScriptUpload.html
	POST /testscript/add
testTargetUpload.html
	POST /testtarget/add
	
Complete API:
PUT 	/public/user + JSON
GET 	/public/device-types

GET 	/device?userId={userId}
GET 	/device/{id}/share

GET 	/user/all
GET 	/user + JSON
GET 	/user?name

GET 	/user/{userId}/stats/hosted
GET 	/user/{userId}/stats/used

GET 	/user/{userId}/testprofile
PUT 	/user/{userId}/testprofile

GET 	/user/{userId}/testconfig
TODO PUT 	/user/{userId}/testconfig + JSON
PUT 	/testconfig + JSON TBR

GET 	/user/{userId}/testrun
PUT 	/user/{userId}/testrun + JSON

GET 	/user/{userId}/devicejob
	Params: testRunId
	or status and deviceId
PUT 	/user/{userId}/devicejob/{deviceJobId}/result
GET 	/user/{userId}/devicejob/{deviceJobId}/result

GET /user/{userId}/testscript
POST /user/{userId}/testscript

GET /user/{userId}/testtarget
POST /user/{userId}/testtarget

GET /checkout/testpackage?deviceJobId={deviceJobId}
GET /checkout/testpackage.zip?deviceJobId={deviceJobId}

PUT 	/device
PUT 	/device/type
POST 	/device/{deviceId}
GET 	/device/{id}/share
PUT 	/device/{id}/share/{userShareWithId}


Testing procedure:
execute: maven tests

--->drops existing CBT database
--->creates database names "cbt" from src/main/resources/cbt.sql
--->generate JOOQ classes using created database
--->executes tests
--->removes database