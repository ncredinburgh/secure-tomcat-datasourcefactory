package com.ncr.tomcat;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.IOException;
import java.util.Properties;

public class KeyFile implements KeyLocator {

	public static final String PROP_KEY_FILENAME = "keyFilename";
	
	private String keyFilename;
	
	@Override
	public void configure(Properties properties) throws DecryptionException {
		keyFilename = properties.getProperty(PROP_KEY_FILENAME);
	}
	
	@Override
	public byte[] locateKey() throws DecryptionException {
		try {
			validate();
			
			return readAllBytes(get(keyFilename));
		} catch (IOException e) {
			throw new DecryptionException(e);
		}
	}
	
	private void validate() throws DecryptionException {
		if (keyFilename == null) {
			throw new DecryptionException("Property '" + PROP_KEY_FILENAME + "' not specified");
		}
	}
}
