package com.github.ncredinburgh.tomcat;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;

import java.util.Properties;

import com.github.ncredinburgh.tomcat.DecryptionException;
import com.github.ncredinburgh.tomcat.KeyLocator;

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
