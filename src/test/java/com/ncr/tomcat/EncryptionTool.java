package com.ncr.tomcat;

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
		byte[] keyBytes = generateKeyBytes("DES", 56);
		System.out.println("Generated key (Base64): " + printBase64Binary(keyBytes));
		
		File keyFile = new File("src/test/resources/com/ncr/tomcat/Example-DES-56.key");
		System.out.println("Writing new key to file: " + keyFile.getCanonicalPath());
		Files.write(keyFile.toPath(), keyBytes);
		
		String clearPassword = "Sup3rS3cr3tP455w0rd";
		System.out.println("Clear-text password: " + clearPassword);

		System.out.println("Encrypting password...");
		byte[] cipherBytes = encryptPassword(clearPassword, "DES", "ECB", "PKCS5PADDING", keyFile);
		
		File passwordFile = new File("src/test/resources/com/ncr/tomcat/Example-DES-56.password");
		System.out.println("Writing encrypted password to file: " + passwordFile.getCanonicalPath());
		Files.write(passwordFile.toPath(), cipherBytes);
		
		System.out.println("Cipher-text password (Base64): " + printBase64Binary(cipherBytes));
	}



}
