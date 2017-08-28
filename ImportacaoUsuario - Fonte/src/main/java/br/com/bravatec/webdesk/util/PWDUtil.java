package br.com.bravatec.webdesk.util;

import java.security.MessageDigest;

public final class PWDUtil {
	
	public static String md5(String str) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(str.getBytes());
		byte[] hashMd5 = md.digest();
		
		return stringHexa(hashMd5);
	}

	private static String stringHexa(byte[] bytes) {
		   StringBuilder s = new StringBuilder();
		   for (int i = 0; i < bytes.length; i++) {
		       int parteAlta = ((bytes[i] >> 4) & 0xf) << 4;
		       int parteBaixa = bytes[i] & 0xf;
		       if (parteAlta == 0) s.append('0');
		       s.append(Integer.toHexString(parteAlta | parteBaixa));
		   }
		   return s.toString();
		}
}
