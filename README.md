[![Build Status](https://travis-ci.org/ncredinburgh/secure-tomcat-datasourcefactory.svg?branch=master)](https://travis-ci.org/ncredinburgh/secure-tomcat-datasourcefactory)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.ncredinburgh/secure-tomcat-datasourcefactory/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.ncredinburgh/secure-tomcat-datasourcefactory)

Secure Tomcat DataSourceFactory
===============================

This library provides a drop in replacement for the standard Tomcat DataSourceFactory that allows the database connection password to be encrypted using a symmetric key for the purposes of security.  This datasource uses the standard [Cipher](http://docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html) class from Java Cryptography Architecture to perform the decrytion.  As such all the algorithms installed in the JVM are available to use.  By default all JVM vendors must support the [standard algorithms](http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#impl). Consult your vendor's documentation for any further algorithm support.

This library may also be run from the command line to generate an encryption key and encrypted password to be used in the Tomcat configuration.

The secure Tomcat DataSourceFactory is tested using the GitHub project [secure-datasourcefactory-test](https://github.com/grantjforrester/secure-datasourcefactory-test). The following databases have been tested:

* Postgres 9.6
* Oracle 12c    


Getting Started
---------------
### Download the Library
* Download the latest version of the library using the link in the Maven Central badge at the top of this page.

### Generate Key and Encrypted Password
* Generate a new random encryption key to a file 
e.g to create a new 128-bit AES key run the command:

```
$ java -jar secure-tomcat-datasourcefactory-0.2.jar generateKey AES 128 /some/super/secure/location/keyfile
New key written to file: /some/super/secure/location/keyfile 
```

* The encryption key is now in the specified file e.g. `/some/super/secure/location/keyfile`

* Generate the encrypted password in [Base64](https://en.wikipedia.org/wiki/Base64) encoding e.g. using AES/ECB/PKCS5PADDING

```
$ java -jar secure-tomcat-datasourcefactory-0.2.jar encryptPassword mypassword AES ECB PKCS5PADDING /some/super/secure/location/keyfile`
Encrypted password: O+JXajIzZS5Hi2+3vpdeqw==
```


Configure Tomcat
----------------

1. Copy the JAR file to the folder `${TOMCAT_HOME}/lib`

2. Make the following changes to your JNDI datasource in `${TOMCAT_HOME}/conf/context.xml` 
    * Add `factory="com.github.ncredinburgh.tomcat.SecureDataSourceFactory"`
    
    * Replace existing clear text `password` value with [Base64](https://en.wikipedia.org/wiki/Base64) encoded encrypted password e.g. `password="O+JXajIzZS5Hi2+3vpdeqw=="`
    
    * Add algorithm details to `connectionProperties` e.g. `algorithm=AES;mode=ECB;padding=PKCS5PADDING`
    
    * Add location of keyfile to `connectionProperties` e.g.`keyFilename=/some/super/secure/location/keyfile`
3. Congratulations your done!  


### Tomcat Configuration Examples
 
These examples are based on the examples given in [Tomcat JNDI Datasource HOW-TO](https://tomcat.apache.org/tomcat-7.0-doc/jndi-datasource-examples-howto.html)

#### Oracle datasource using encrypted password  

	<Resource name="jdbc/myoracle" auth="Container"
	  factory="com.github.ncredinburgh.tomcat.SecureDataSourceFactory"
	  type="javax.sql.DataSource" driverClassName="oracle.jdbc.OracleDriver"
	  url="jdbc:oracle:thin:@127.0.0.1:1521:mysid"
	  maxActive="20" maxIdle="10" maxWait="-1"
	  username="myuser" password="C0iZc6o+6xqr0NggmuTo9gRtfowg0kyM8fqNQEJwAZE="
      connectionProperties="algorithm=AES;mode=ECB;padding=PKCS5PADDING;keyFilename=/some/super/secure/location/keyfile"/>

#### Postgres datasource using encrypted password 

    <Resource name="jdbc/postgres" auth="Container"
      factory="com.github.ncredinburgh.tomcat.SecureDataSourceFactory"
      type="javax.sql.DataSource" driverClassName="org.postgresql.Driver"
      url="jdbc:postgresql://127.0.0.1:5432/mydb"
      maxActive="20" maxIdle="10" maxWait="-1"
      username="myuser" password="C0iZc6o+6xqr0NggmuTo9gRtfowg0kyM8fqNQEJwAZE="
      connectionProperties="algorithm=AES;mode=ECB;padding=PKCS5PADDING;keyFilename=/some/super/secure/location/keyfile"/>   


Reference
---------

### Tomcat Configuration Reference 

The `SecureDataSourceFactory` extends the [standard Tomcat DataSource](https://tomcat.apache.org/tomcat-7.0-doc/api/org/apache/tomcat/jdbc/pool/DataSourceFactory.html) and is configured using the DataSource property `connectionProperties` with the following values :

* `algorithm`

 Required. The name of the algorithm used to decrypt the password. Must be the name of one of the algorithm installed in the JVM using the Java Cryptography Architecture.

* `mode`

 Optional. The mode of the algorithm (where appropriate) to decrypt the password. Default value is `NONE`. For valid options consult the algorithm documentation.

* `padding`

 Optional. The padding used with the algorithm (where appropriate) to decrypt the password. Default value is `NoPadding`. For valid options consult the algorithm documentation.

* `keyFilename`

 Required*. The location of the file holding the secret key to be used with the algorithm to decrypt the password.  This file must exist and be readable by the user of the Tomcat process.

* `keyLocator`

 Optional. The locator used to provide the decryption key. Default value is `com.github.ncredinburgh.tomcat.KeyFile`.  Must be a class implementing the `KeyLocator` interface in Tomcat's classpath. See "Alternative KeySources" section below.

\* Only required if the default key locator `KeyFile` is being used  is used. See "Alternative Key Sources" section below.

### Command Line Reference

The `SecureTomcatDataSourceFactory` library can be run on the command line to perform the necessary setup steps to create an encryption key and an encrypted password.

You can execute the `SecureTomcatDataSourceFactory` using the command line:

```
$ java -jar secure-tomcat-datasourcefactory-0.2.jar <command> <options>
``` 

The following commands are supported:

* `listKeyGenerators`

 Displays the names of the algorithms installed on this JVM that may be used to 
 generate keys.

* `listCiphers`

 Displays the names of the algorithms installed on this JVM that may be used to encrypt passwords.

* `generateKey <algorithm> <keySize> <keyFilename>`

 Generates a random encryption key using the specified algorithm and key size into the named key file.

* `encryptPassword <password> <algorithm> <mode> <padding> <keyFilename>`

 Encrypts the given password using the specified algorithm/mode/padding with the named  key file.


Security Advice
---------------
Encrypting a password and storing the secret key on the same server doesn't improve security.  It only adds one more step to the malicious users job: namely decrypting your password with the key in the file. This is "security through obscurity". Likewise, embedding the secret key in a Java class in a JAR file doesn't improve security.  The malicious user can decompile the class, retrieve the key and decrypt the encrypted password.

The range of solutions to this problem is outwith the scope of this discussion but if you want to use the default `KeyFile` implementation of the `KeyLocator` a more secure approach would be to ensure that the key file is *only* present when Tomcat *starts*.  This could be achieved through storing the key file on a temporary filesystem that is only mounted during Tomcat startup - either physically through a USB memory stick or logically through a remote filesystem. After Tomcat startup is complete the filesystem is unmounted and the key is no longer available to the malicious user.  The decrypted key is maintained only in the memory of the running Tomcat server.


Alternative Key Sources
-----------------------
By default the `SecureTomcatDataSourceFactory` expects to locate the decryption key in a file. This is because, by default, the decryptor is configured with a *key locator* of type `KeyFile`.  The `KeyFile` locator requires the path to the key file in the property `keyFilename`.

Other more sophisticated implementations of `KeyLocator` can be written to interface with more advanced secret stores that offer features like one time use passwords and time limited access.


Building Your Own KeyLocator
----------------------------
* Create a new Java project
* Add this library as a dependency of your build using the Maven coordinates from the Maven Central badge at the top of this page.
* Create a new class that implements the interface `com.github.ncredinburgh.tomcat.KeyLocator`
* Write your `locateKey()` method to fetch the secret key for decrypting the configured password.
* Build your project into a JAR file
* Deploy your JAR file alongside this library in the folder `${TOMCAT_HOME}/lib`
* Change your JNDI datasource configuration such that the `connectionsProperties` value include the `keyLocator` property with the fully qualified class name of your new class.
* Your done!


### Postgres datasource using custom KeyLocator

    <Resource name="jdbc/postgres" auth="Container"
      factory="com.github.ncredinburgh.tomcat.SecureDataSourceFactory"
      type="javax.sql.DataSource" driverClassName="org.postgresql.Driver"
      url="jdbc:postgresql://127.0.0.1:5432/mydb"
      maxActive="20" maxIdle="10" maxWait="-1"
      username="myuser" password="C0iZc6o+6xqr0NggmuTo9gRtfowg0kyM8fqNQEJwAZE="
      connectionProperties="algorithm=AES;mode=ECB;padding=PKCS5PADDING;keyLocator=com.example.MyKeyLocator"/>     

Contributing
------------

All contributions are welcome. Just fork this repository and send us a merge request.  Just make sure your code meets the following requirements:

* You follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
* All the unit tests pass when running `mvn test`



Credits
-------
This project was inspired by an original article on [JDev](https://www.jdev.it) called ["Encrypting passwords in Tomcat"](https://www.jdev.it/encrypting-passwords-in-tomcat/).   


Releases
--------

### 0.2

- Library can be run from the command line to produce new encryption key and to encrypt password.
- Updates to README including instructions on how to run library on command line.

### 0.1

- First public release
