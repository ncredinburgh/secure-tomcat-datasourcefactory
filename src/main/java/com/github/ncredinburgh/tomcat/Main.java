package com.github.ncredinburgh.tomcat;

import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import static javax.crypto.Cipher.ENCRYPT_MODE;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Main {
	
	public static void main(String[] args) throws Exception {
		Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
		try {
			String command = arguments.remove();
			if (command.equals("listKeyGenerators")) listKeyGenerators(arguments);
			else if (command.equals("listCiphers")) listCiphers(arguments);
			else if (command.equals("generateKey")) generateKey(arguments);
			else if (command.equals("encryptPassword")) encryptPassword(arguments);
			else printUsage();
		} catch (NoSuchElementException e) {
			printUsage();
		}
	}

	private static void listKeyGenerators(Queue<String> arguments) {
		for (Provider provider : Security.getProviders()) {
            for (Provider.Service service : provider.getServices()) {
                if (service.getType().equals("KeyGenerator")) {
                	System.out.println("Provider: " + provider.getName() + "  Algorithm: " + service.getAlgorithm());
                }
            }
        }
	}
	
	private static void listCiphers(Queue<String> arguments) {
		for (Provider provider : Security.getProviders()) {
            for (Provider.Service service : provider.getServices()) {
                if (service.getType().equals("Cipher")) {
                	System.out.println("Provider: " + provider.getName() + "  Algorithm: " + service.getAlgorithm());
                }
            }
        }
	}

	private static void generateKey(Queue<String> arguments) throws GeneralSecurityException, IOException {
		try {
			String algorithm = arguments.remove();
			int keySize = Integer.parseInt(arguments.remove());
			String keyFilename = arguments.remove();
			
			byte[] keyBytes = generateKey(algorithm, keySize);
			File keyFile = new File(keyFilename);
			Files.write(keyFile.toPath(), keyBytes);
			System.out.println("New key written to file: " + keyFilename);
		} catch (NoSuchElementException e) {
			printUsage();
		}
	}
	
	private static byte[] generateKey(String algorithm, int keySize) throws GeneralSecurityException {
		KeyGenerator generator = KeyGenerator.getInstance(algorithm);
		generator.init(keySize);
		SecretKey key = generator.generateKey();
		return key.getEncoded();
	}
	
	private static void encryptPassword(Queue<String> arguments) throws GeneralSecurityException, IOException {
		try {
			String password = arguments.remove();
			String algorithm = arguments.remove();
			String mode = arguments.remove();
			String padding = arguments.remove();
			String keyFilename = arguments.remove();
			
			byte[] key = readAllBytes(get(keyFilename));
			System.out.println("Encrypted password: " + printBase64Binary(encryptPassword(password, algorithm, mode, padding, key)));
		} catch (NoSuchElementException e) {
			printUsage();
		}
	}
	
	public static byte[] encryptPassword(String password, String algorithm, String mode, String padding, byte[] key) throws GeneralSecurityException {
		SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);
		Cipher cipher = Cipher.getInstance(format("%s/%s/%s", algorithm, mode, padding));
        cipher.init(ENCRYPT_MODE, keySpec);
        byte[] cipherText = cipher.doFinal(password.getBytes());
        
        return cipherText;
	}
	
	private static void printUsage() {
		System.out.println("Usage: secure-tomcat-datasourcefactory <command> <options>");
		System.out.println("Commands:");
		System.out.println("    listKeyGenerators");
		System.out.println("    listCiphers");
		System.out.println("    generateKey <algorithm> <keySize> <keyFilename>");
		System.out.println("    encryptPassword <password> <algorithm> <mode> <padding> <keyFilename>");
	}
}
