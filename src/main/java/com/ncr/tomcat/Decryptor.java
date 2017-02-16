package com.ncr.tomcat;

public interface Decryptor {
	
	public String decrypt(String cipherText) throws DecryptionException;
	
}
