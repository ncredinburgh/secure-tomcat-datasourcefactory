package com.ncr.tomcat;

import static java.lang.String.format;
import static javax.crypto.Cipher.DECRYPT_MODE;

import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * A configurable {@link Decryptor} that allows a given cipher text to be decrypted using a specified algorithm and key file.
 * The decryptor can be configured through the properties provided in the constructor.   The following properties are available
 * 
 * <dl>
 * 	<dt>algorithm</dt>
 *  <dt>mode</dt>
 *  <dt>padding</dt>
 *  <dt>keyFilename</dt>
 * </dl>
 * 
 * The file specified by keyFilename must exist and be readable by the Tomcat user.
 */
public class ConfigurableDecryptor implements Decryptor {

	public static final String PROP_ALGORITHM = "algorithm";
	public static final String PROP_MODE = "mode";
	public static final String PROP_PADDING = "padding";
	public static final String PROP_KEYLOCATOR = "keyLocator";

	public static final String DEFAULT_KEYLOCATOR = "com.ncr.tomcat.KeyFile";
	public static final String DEFAULT_MODE = "NONE";
	public static final String DEFAULT_PADDING = "NoPadding";
	
	private String algorithm;
	private String mode;
	private String padding;
	private String keyLocatorClass;
	private KeyLocator keyLocator;
	
	public void configure(Properties properties) throws DecryptionException {
		algorithm = properties.getProperty(PROP_ALGORITHM);
		mode = properties.getProperty(PROP_MODE, DEFAULT_MODE);
		padding = properties.getProperty(PROP_PADDING, DEFAULT_PADDING);
		keyLocatorClass = properties.getProperty(PROP_KEYLOCATOR, DEFAULT_KEYLOCATOR);

		keyLocator = createLocator(keyLocatorClass);
		keyLocator.configure(properties);
	}
	
	public byte[] decrypt(byte[] cipherBytes) throws DecryptionException {
		try {
			validate();
			
			String transformation = format("%s/%s/%s", algorithm, mode, padding);
	    	SecretKeySpec keySpec = new SecretKeySpec(keyLocator.locateKey(), algorithm);
	    	
			Cipher cipher = Cipher.getInstance(transformation);
	        cipher.init(DECRYPT_MODE, keySpec);

	        return cipher.doFinal(cipherBytes);
	        
		} catch (GeneralSecurityException e) {
			throw new DecryptionException(e);
		}
	}
	
	private void validate() throws DecryptionException {
		if (algorithm == null) {
			throw new DecryptionException("Property '" + PROP_ALGORITHM +"' not specified");
		}
	}
	
	private KeyLocator createLocator(String keyLocator) throws DecryptionException {
		try {
			return (KeyLocator) Class.forName(keyLocator).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new DecryptionException(e);
		}
	}
}
