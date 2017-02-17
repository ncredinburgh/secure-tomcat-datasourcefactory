Secure Tomcat DataSourceFactory
===============================

This library provides a drop in replacement for the standard Tomcat DataSourceFactory that allows the database connection password to be encrypted using a symmetric key for the purposes of security.  This datasource uses the standard [Cipher](http://docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html) class from Java Cryptography Architecture to perform the decrytion.  As such all the algorithms installed in the JVM are available to use.  By default all JVM vendors must support the [standard algorithms](http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#Cipher). Consult your vendor's documentation for any further algorithm support.   

Usage
-----
* Download the JAR file secure-datasourcefactory.jar
* Copy the JAR file to the folder `{TOMCAT_HOME}/lib`
* Make the following changes to your JNDI datasource in `{TOMCAT_HOME}/context.xml` 
    * Add `factory="com.ncr.tomcat.SecureDataSourceFactory"`
    * Replace existing `password` with [Base64](https://en.wikipedia.org/wiki/Base64) encoded encrypted password e.g. `encryptedPassword="C0iZc6o+6xqr0NggmuTo9gRtfowg0kyM8fqNQEJwAZE="`
    * Add algorithm details e.g. `algorithm="AES" mode="ECB" padding="PKCS5PADDING"`
    * Add location of keyfile e.g, `keyFilename="/some/super/secure/location/keyfile"`
* Congratulations your done!
          

Alternative Key Sources
-----------------------

By default the `SecureDataSourceFactory` expects to locate the decryption key in a file. This is because, by default, the decryptor is configured with a *key locator* of type `KeyFile`.  The `KeyFile` locator requires the path to the key file in the property `keyFilename`.

Alternative key locator implementations can be used by specifying the name of a *key locator* class in the property `keyLocator`.  To use your own key locator create a class that implements the `KeyLocator` interface and make sure it is in a JAR file in `{TOMCAT_HOME}/lib`.  This approach can be used to use keys compiled into JAR files or even from remote key stores.  


Reference
---------
The `SecureDataSourceFactory` extends the [standard Tomcat DataSource](https://tomcat.apache.org/tomcat-7.0-doc/api/org/apache/tomcat/jdbc/pool/DataSourceFactory.html) with the following properties:

* `encryptedPassword`: Optional.  The Base64 encoded encryted password. We use Base64 encoding as the result of the encryption algorithm is binary data which we are including in a text file ``{TOMCAT_HOME}/context.xml``. If you omit the `encryptedPassword` property and instead provide the regular `password` property the data source will operate like the standard datasource providing no decryption.
* `algorithm`: Required - if `encryptedPassword` is used. The name of the algorithm used to decrypt the password. Must be the name of one of the algorithm installed in the JVM using the Java Cryptography Architecture.
* `mode`: Optional. The mode of the algorithm (where appropriate) to decrypt the password. Default value is `NONE`. For valid options consult the algorithm documentation.
* `padding`:  Optional. The padding used with the algorithm (where appropriate) to decrypt the password. Default value is `NoPadding`. For valid options consult the algorithm documentation.
* `keyFilename`: Required - if the default `KeyFile` locator is used. The location of the file holding the secret key to be used with the algorithm to decrypt the password.  This file must exist and be readble by the user of the Tomcat process.
* `keyLocator`: Optional. The locator used to provide the decryption key. Default value is `com.ncr.tomcat.KeyFile`.  Must be a class implementing the `KeyLocator` interface in Tomcat's classpath.
 

          
Example
------- 

These examples are based on the examples given in [Tomcat JNDI Datasource HOW-TO](https://tomcat.apache.org/tomcat-7.0-doc/jndi-datasource-examples-howto.html)

### Oracle datasource using keyfile  

	<Resource name="jdbc/myoracle" auth="Container"
      factory="com.ncr.tomcat.SecureDataSourceFactory"
      type="javax.sql.DataSource" driverClassName="oracle.jdbc.OracleDriver"
      url="jdbc:oracle:thin:@127.0.0.1:1521:mysid"
      maxActive="20" maxIdle="10" maxWait="-1"
      username="myuser" encryptedPassword="C0iZc6o+6xqr0NggmuTo9gRtfowg0kyM8fqNQEJwAZE="
      algorithm="AES" mode="ECB" padding="PKCS5PADDING"
      keyFilename="/some/super/secure/location/keyfile"/>
      
### Postgres datasource using compiled key

    <Resource name="jdbc/postgres" auth="Container"
      factory="com.ncr.tomcat.SecureDataSourceFactory"
      type="javax.sql.DataSource" driverClassName="org.postgresql.Driver"
      url="jdbc:postgresql://127.0.0.1:5432/mydb"
      maxActive="20" maxIdle="10" maxWait="-1"
      username="myuser" encryptedPassword="C0iZc6o+6xqr0NggmuTo9gRtfowg0kyM8fqNQEJwAZE="
      algorithm="AES" mode="ECB" padding="PKCS5PADDING"
      keyLocator="com.example.keyClass"/>          