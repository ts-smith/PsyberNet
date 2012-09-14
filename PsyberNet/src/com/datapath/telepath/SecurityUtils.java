package com.datapath.telepath;

import java.math.BigInteger;
import java.util.regex.Pattern;

public class SecurityUtils {
	static String bin2hex(byte[] data) {
		  return String.format("%0" + (data.length * 2) + 'x', new BigInteger(1, data));
		}
	//regex for validating names
	static Pattern p = Pattern.compile("[a-zA-Z0-9 _-]+");

}
