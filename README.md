Secure Tomcat DataSourceFactory
===============================

This library provides a drop in replacement for the standard Tomcat DataSourceFactory that allows the database connection password to be encrypted using a symmetric key for the purposes of security.  This datasource uses the standard [Cipher](http://docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html) class from Java Cryptography Architecture to perform the decrytion.  As such all the algorithms installed in the JVM are available to use.  By default all JVM vendors must support the [standard algorithms](http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#Cipher). Consult your vendor's documentation for any further algorithm support.   

Usage
-----
* Download the JAR file secure-datasourcefactory.jar
* Copy the JAR file to the folder ``{TOMCAT_HOME}/lib``
* Make the following changes to your JNDI datasource in ``{TOMCAT_HOME}/context.xml`` 
    * Add ``factory="com.ncr.tomcat.SecureDataSourceFactory"``
    * Use encrypted password e.g. ``password="d4bc96e4b923770d7501e89f89a8e6e4d87bb61f1d2a7939de772a77a221a4db"``
    * Add algorithm details e.g. ``algorithm="AES" mode="CBC" padding="NoPadding"``
    * Add location of keyfile e.g, ``keyFilename="/some/super/secure/location/keyfile"``
* Congratulations your done!
          

Reference
---------
The ``SecureDataSourceFactory`` extends the [standard Tomcat DataSource](https://tomcat.apache.org/tomcat-7.0-doc/api/org/apache/tomcat/jdbc/pool/DataSourceFactory.html) with the following properties:

* ``algorithm``: Required. The name of the algorithm used to decrypt the password. Must be the name of one of the algorithm installed in the JVM using the Java Cryptography Architecture.
* ``mode``: Optional. The mode of the algorithm (where appropriate) to decrypt the password. Default value is ``NONE``. For valid options consult the algorithm documentation.
* ``padding``:  Optional. The padding used with the algorithm (where appropriate) to decrypt the password. Default value is ``NoPadding``. For valid options consult the algorithm documentation.
* ``keyFilename``: Required. The location of the file holding the secret key to be used with the algorithm to decrypt the password.  This file must exist and be readble by the user of the Tomcat process.
          
          
Example
------- 

An example Oracle datasource configuration based on [Tomcat JNDI Datasource HOW-TO](https://tomcat.apache.org/tomcat-7.0-doc/jndi-datasource-examples-howto.html) 

	<Resource name="jdbc/myoracle" auth="Container"
      factory="com.ncr.tomcat.SecureDataSourceFactory"
      type="javax.sql.DataSource" driverClassName="oracle.jdbc.OracleDriver"
      url="jdbc:oracle:thin:@127.0.0.1:1521:mysid"
      maxActive="20" maxIdle="10" maxWait="-1"
      username="myuser" password="d4bc96e4b923770d7501e89f89a8e6e4d87bb61f1d2a7939de772a77a221a4db"
      algorithm="AES" mode="CBC" padding="NoPadding"
      keyFilename="/some/super/secure/location/keyfile"/>          