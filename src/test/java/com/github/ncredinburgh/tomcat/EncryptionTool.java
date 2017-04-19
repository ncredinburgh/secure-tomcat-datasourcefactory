package com.github.ncredinburgh.tomcat;

import static java.lang.String.format;
import static javax.crypto.Cipher.ENCRYPT_MODE;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import java.io.File;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionTool {
	
	private static final String ALGORITHM = "AES";
	private static final int KEY_SIZE = 128;
	private static final String MODE = "ECB";
	private static final String PADDING = "PKCS5PADDING";
	private static final String CLEAR_PASSWORD = "mysecretpassword";
	
	public static byte[] generateKeyBytes(String algorithm, int keySize) throws NoSuchAlgorithmException {
		KeyGenerator generator = KeyGenerator.getInstance(algorithm);
		generator.init(keySize);
		SecretKey key = generator.generateKey();
		
		return key.getEncoded();
	}

	public static byte[] encryptPassword(String password, String algorithm, String mode, String padding, File keyFile) throws Exception {
		byte[] keyBytes = Files.readAllBytes(keyFile.toPath());
		
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, algorithm);
    	
		Cipher cipher = Cipher.getInstance(format("%s/%s/%s", algorithm, mode, padding));
        cipher.init(ENCRYPT_MODE, keySpec);
        byte[] cipherText = cipher.doFinal(password.getBytes());
        
        return cipherText;
	}
	
	
	public static void main(String[] args) throws Exception {
		//DES/CBC/PKCS5Padding (56)
		System.out.println("Generating key...");
		byte[] keyBytes = generateKeyBytes(ALGORITHM, KEY_SIZE);
		System.out.println("Generated key (Base64): " + printBase64Binary(keyBytes));
		
		File keyFile = new File(format("./%s-%d.key", ALGORITHM, KEY_SIZE));
		System.out.println("Writing new key to file: " + keyFile.getCanonicalPath());
		Files.write(keyFile.toPath(), keyBytes);
		
		System.out.println("Clear-text password: " + CLEAR_PASSWORD);

		System.out.println("Encrypting password...");
		byte[] cipherBytes = encryptPassword(CLEAR_PASSWORD, ALGORITHM, MODE, PADDING, keyFile);
		
		File passwordFile = new File(format("./%s-%d.password", ALGORITHM, KEY_SIZE));
		System.out.println("Writing encrypted password to file: " + passwordFile.getCanonicalPath());
		Files.write(passwordFile.toPath(), cipherBytes);
		
		System.out.println("Cipher-text password (Base64): " + printBase64Binary(cipherBytes));
	}



}
