package com.ncr.tomcat;

public class PasswordHolder {

	private static String password;

	public static void setPassword(String password) {
		PasswordHolder.password = password;
	}

	public static String getPassword() {
		return password;
	}
}
