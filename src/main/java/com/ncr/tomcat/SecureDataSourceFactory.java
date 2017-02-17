package com.ncr.tomcat;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;

import java.util.Properties;

import javax.naming.Context;
import javax.sql.DataSource;

import org.apache.tomcat.jdbc.pool.DataSourceFactory;

/**
 * A {@link DataSourceFactory} that supports an encrypted password.
 * If the data source configuration does not contain a <code>password</code> property the data source will look for a property 
 * called <code>encryptedPassword</code> and decrypt it using a {@link ConfigurableDecryptor} object.
 */
public class SecureDataSourceFactory extends DataSourceFactory {

	public String PROP_ENCRYTPEDPASSWORD = "encryptedPasswoed";
	
	private Decryptor decryptor = new ConfigurableDecryptor();
			
	@Override
	public DataSource createDataSource(Properties properties, Context context, boolean XA) throws Exception {
		if (!properties.containsKey(PROP_PASSWORD)) {
			decryptPassword(properties);
		}
		
		return super.createDataSource(properties, context, XA);
	}
	
	private void decryptPassword(Properties properties) throws DecryptionException {
		String cipherPassword = properties.getProperty(PROP_ENCRYTPEDPASSWORD);
		byte[] cipherBytes = parseBase64Binary(cipherPassword);
		
		decryptor.configure(properties);
		String clearPassword = new String(decryptor.decrypt(cipherBytes));
		
		properties.setProperty(PROP_PASSWORD, clearPassword);
	}
}
