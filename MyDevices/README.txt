it is not a real maven project, but use pom.xml to download dependency only.
run following command to download jars into one folder: 
mvn dependency:copy-dependencies -DoutputDirectory=libs