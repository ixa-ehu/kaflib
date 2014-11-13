KAFLIB
======

Kaflib is a library which allows creating, editing and reading NAF (and KAF) documents.


Compiling the library with maven
================================

To compile the library, clone this repository and do 'mvn package'. It will create a JAR into 'target' directory. There are two branches in the repository:
* naf: the library version to work with NAF documents.
* kaf: the library version to work with KAF documents.


Using the library as a maven dependency
=======================================

You can find [Kaflib](http://search.maven.org/#search|ga|1|kaflib) in the [Maven Central Repository](http://search.maven.org/). It is split in two different artifacts: the NAF version (kaflib-naf) and the KAF version (kaflib).

This is what you need to put in your pom.xml file to make use of kaflib:

Maven dependency for the NAF version:
````shell
<dependency>
    <groupId>com.github.ixa-ehu</groupId>
    <artifactId>kaflib-naf</artifactId>
    <version>1.0.0</version> <!-- Set the last version here -->
</dependency>
````

Maven dependency for the KAF version:
````shell
<dependency>
    <groupId>com.github.ixa-ehu</groupId>
    <artifactId>kaflib</artifactId>
    <version>1.0.0</version> <!-- Set the last version here -->
</dependency>
````


Contact information
===================

````shell
Zuhaitz Beloki
IXA Group
University of the Basque Country (UPV/EHU)
zuhaitz.beloki@ehu.es
````
