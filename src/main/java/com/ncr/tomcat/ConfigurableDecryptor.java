package com.ncr.tomcat;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * A configurable {@link Decryptor} that allows a given cipher text to be decrypted using a specified algorithm and key file.
 * The decryptor can be configured through the properties provided in the constructor.   The following properties are availabe
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

	public static final String PROPERTY_ALGORITHM = "algorithm";
	public static final String PROPERTY_MODE = "mode";
	public static final String PROPERTY_PADDING = "padding";
	public static final String PROPERTY_KEY_FILE = "keyFilename";
	
	private String algorithm;
	private String mode;
	private String padding;
	private String keyFilename;
	
	public ConfigurableDecryptor(Properties properties) throws DecryptionException {
		algorithm = properties.getProperty(PROPERTY_ALGORITHM);
		mode = properties.getProperty(PROPERTY_MODE, "NONE");
		padding = properties.getProperty(PROPERTY_PADDING, "NoPadding");
		keyFilename = properties.getProperty(PROPERTY_KEY_FILE);
		validateProperties();
	}
	
	public String decrypt(String cipherText) throws DecryptionException {
		try {
			
			String transformation = String.format("%s/%s/%s", algorithm, mode, padding);
	    	SecretKeySpec keySpec = new SecretKeySpec(readAllBytes(get(keyFilename)), algorithm);
	    	
			Cipher cipher = Cipher.getInstance(transformation);
	        cipher.init(Cipher.DECRYPT_MODE, keySpec);
	        byte[] clearText = cipher.doFinal(cipherText.getBytes());
	        
	        return new String(clearText);
	        
		} catch (GeneralSecurityException|IOException e) {
			throw new DecryptionException(e);
		}
	}

	private void validateProperties() throws DecryptionException {
		if (keyFilename == null) throw new DecryptionException("No encyption 'keyFilename' specified");
		if (algorithm == null) throw new DecryptionException("No encyption 'algorithm' specified");		
	}
}
