package com.github.ncredinburgh.tomcat;

import java.util.Properties;

public interface KeyLocator {
	
	public void configure(Properties properties) throws DecryptionException;

	public byte[] locateKey() throws DecryptionException;
}
