package com.riskcontrol.util;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;


public class EncryptionUtil {

	private static final String KEY = "hr248cn4563413";

	public static String encryptPassword(String baseSalt, String inputPassword) {
		StringBuilder sb = new StringBuilder("");
		sb.append(inputPassword);
		for (int i = 1, length = baseSalt.length(); i < length;) {
			sb.append(baseSalt.charAt(i));
			i += i;
		}
		return sb.toString();
	}

	public static String hashPassword(String inputPassword) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		String result = "";
        byte[] bytes = md5.digest(inputPassword.getBytes("UTF-8"));
        for (byte b : bytes) {
            String temp = Integer.toHexString(b & 0xff);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            result += temp;
        }
        return result;
	}

	public static boolean verifyPassword(String salt,String password1 ,String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		if(password.equals(hashPassword(encryptPassword(salt,password1)))){
			return true;
		}
		return false;
	}

	public static String saltGenerator() {
		return randomString(32);
	}

	public static String randomString(int length) {
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
	}

	public static String decodePwd(String encodePwd) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(KEY.getBytes());
        kgen.init(128, secureRandom);
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
        Cipher cipher = Cipher.getInstance("AES");// 创建密码器

		cipher.init(Cipher.DECRYPT_MODE, key);// 初始化解密器
        byte[] decryptFrom = parseHexStr2Byte(encodePwd);
        byte[] result1 = cipher.doFinal(decryptFrom);
        return new String(result1);
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		String  abc = "123456";

        System.out.println(EncryptionUtil.hashPassword(abc));

	}

	/**
     * 二进制转换成16进制，加密后的字节数组不能直接转换为字符串
     */
    static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 16进制转换成二进制
     */
    static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
        	return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}
