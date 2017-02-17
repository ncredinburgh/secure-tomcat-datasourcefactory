package com.ncr.tomcat;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;

import java.util.Properties;

public class DES56CompiledKey implements KeyLocator {

	private static final String base64DES56Key = "ecH4RqJrx2s=";
	
	@Override
	public void configure(Properties properties) throws DecryptionException {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] locateKey() throws DecryptionException {
		return parseBase64Binary(base64DES56Key);
	}

}
