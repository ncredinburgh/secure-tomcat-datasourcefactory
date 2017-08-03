package com.github.ncredinburgh.tomcat;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

public class TestConfigurableDecryptor {
	private static final String RESOURCE_DIR = "src/test/resources/com/github/ncredinburgh/tomcat/";
	
	private Decryptor testee;
	private Properties decryptorProperties;
	
	@Before
	public void setUp() {
		testee = new ConfigurableDecryptor();
		decryptorProperties = new Properties();
	}
	
	@Test
	public void shouldDecryptEncryptedPasswordUsingKeyFile() throws Exception {
		File keyFile = new File(RESOURCE_DIR + "Example-AES-128.key"); 
		String encryptedPassword = "C0iZc6o+6xqr0NggmuTo9gRtfowg0kyM8fqNQEJwAZE=";
		String expectedPassword = "Sup3rS3cr3tP455w0rd";

		byte[] passwordBytes = parseBase64Binary(encryptedPassword);
		
		decryptorProperties.put("algorithm", "AES");
		decryptorProperties.put("mode", "ECB");
		decryptorProperties.put("padding", "PKCS5PADDING");
		decryptorProperties.put("keyFilename", keyFile.getCanonicalPath());
		testee.configure(decryptorProperties);

		byte[] clearBytes = testee.decrypt(passwordBytes);
		
		assertThat(expectedPassword, is(equalTo(new String(clearBytes))));
	}
	
	@Test
	public void shouldDecryptEncryptedPasswordUsingCompiledKey() throws Exception {
		String encryptedPassword = "9feb2+wj47QYKChmbeEnY28a1krO2H7k";
		String expectedPassword = "Sup3rS3cr3tP455w0rd"; 
		byte[] passwordBytes = parseBase64Binary(encryptedPassword);
		
		decryptorProperties.put("algorithm", "DES");
		decryptorProperties.put("mode", "ECB");
		decryptorProperties.put("padding", "PKCS5PADDING");
		decryptorProperties.put("keyLocator", "com.github.ncredinburgh.tomcat.DES56CompiledKey");
		
		testee.configure(decryptorProperties);
		byte[] clearBytes = testee.decrypt(passwordBytes);
		
		assertThat(expectedPassword, is(equalTo(new String(clearBytes))));
	}
}
