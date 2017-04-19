package com.github.ncredinburgh.tomcat;

import java.util.Properties;

public interface Decryptor {
	
	public void configure(Properties properties) throws DecryptionException;

	public byte[] decrypt(byte[] cipherBytes) throws DecryptionException;

}
