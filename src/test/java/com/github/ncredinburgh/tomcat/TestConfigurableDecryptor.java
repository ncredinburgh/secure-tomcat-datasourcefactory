package com.github.ncredinburgh.tomcat;

import static java.nio.file.Files.readAllBytes;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.github.ncredinburgh.tomcat.ConfigurableDecryptor;
import com.github.ncredinburgh.tomcat.Decryptor;

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
		String expectedPassword = "Sup3rS3cr3tP455w0rd";
		File keyFile = new File(RESOURCE_DIR + "Example-AES-128.key"); 
		File passwordFile = new File(RESOURCE_DIR + "Example-AES-128.password");
		byte[] passwordBytes = readAllBytes(passwordFile.toPath());
		
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
		String expectedPassword = "Sup3rS3cr3tP455w0rd"; 
		File passwordFile = new File(RESOURCE_DIR + "Example-DES-56.password");
		byte[] passwordBytes = readAllBytes(passwordFile.toPath());
		
		decryptorProperties.put("algorithm", "DES");
		decryptorProperties.put("mode", "ECB");
		decryptorProperties.put("padding", "PKCS5PADDING");
		decryptorProperties.put("keyLocator", "com.github.ncredinburgh.tomcat.DES56CompiledKey");
		
		testee.configure(decryptorProperties);
		byte[] clearBytes = testee.decrypt(passwordBytes);
		
		assertThat(expectedPassword, is(equalTo(new String(clearBytes))));
	}
}
