KAFLIB
======

Kaflib is a library which allows creating, editing and reading KAF or NAF documents.


Compiling library with maven
============================

To compile the library, clone this repository and do 'mvn package'. It will create a JAR into 'target' directory. There are two branches in the repository: master and naf:
* master: the library version to work with KAF documents.
* naf: the library version to work with NAF documents.


Using the library as a maven dependency
=======================================

Another github project exists so as to use the library as a maven repository:
* https://github.com/ixa-ehu/kaflib-mvn-repo

This is the code needed to set the dependency:

Maven repository:
````shell
<repository>
  <id>ixa-ehu.kaflib</id>
  <url>https://raw.github.com/ixa-ehu/kaflib-mvn-repo/master</url>
  <snapshots>
    <enabled>true</enabled>
    <updatePolicy>always</updatePolicy>
  </snapshots>
</repository>
````

Maven dependency for KAF version:
````shell
<dependency>
  <groupId>ixa.kaflib</groupId>
  <artifactId>kaflib</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
````

Maven dependency for NAF version:
````shell
<dependency>
  <groupId>ixa.kaflib</groupId>
  <artifactId>kaflib-naf</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
````


Editing maven repository
========================

Follow these steps to edit the library in the maven repository:
* Both projects are needed in your local system (i.e., source-proj and repo-proj)
* Edit \<internal.repo.path\> parameter in the pom.xml file, and set your repo-proj's path.
* In source-proj, edit the code either in master (kaf) or naf branch and do 'mvn deploy'. It'll create a new JAR (among other stuff) inside repo-proj.
* Go to repo-proj, commit all changes and push them to github.
* Changes in source-proj should also be pushed to github.


Contact information
===================

````shell
Zuhaitz Beloki
IXA Group
University of the Basque Country (UPV/EHU)
zuhaitz.beloki@ehu.es
````
