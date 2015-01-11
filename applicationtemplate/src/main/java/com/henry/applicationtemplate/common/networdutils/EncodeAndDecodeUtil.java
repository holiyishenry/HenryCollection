package com.henry.applicationtemplate.common.networdutils;

import java.util.Arrays;
import java.util.Random;

public class EncodeAndDecodeUtil {	
	String key = "";
	
	public EncodeAndDecodeUtil(){
//        this.key = MyApplication.getAppInstance().localEncodeKey;
    }
	
	/**
	 * 把字符串加密成16进制字符串
	 * @param keys 加密密钥
	 * @param strInput 需要加密的字符串
	 * @return 加密后的字符串
	 * @author dongxr
	 */
	public String encryptToHex(String keys, String strInput) {
		char[] keyBytes = new char[256];
		char[] cypherBytes = new char[256];
		for (int i = 0; i < 256; i++) {
			keyBytes[i] = keys.charAt(i % keys.length());
			cypherBytes[i] = (char) i;
		}
		int jump = 0;
		for (int i = 0; i < 256; i++) {
			jump = jump + cypherBytes[i] + keyBytes[i] & 0xFF;
			char tmp = cypherBytes[i];
			cypherBytes[i] = cypherBytes[jump];
			cypherBytes[jump] = tmp;
		}
		int i = 0;
		jump = 0;
		String result = "";
		for (int x = 0; x < strInput.length(); x++) {
			i = i + 1 & 0xFF;
			char tmp = cypherBytes[i];
			jump = jump + tmp & 0xFF;
			char t = (char) (tmp + cypherBytes[jump] & 0xFF);
			cypherBytes[i] = cypherBytes[jump];
			cypherBytes[jump] = tmp;
			try {
				result = result
						+ bytesArrToHex(new byte[] { (byte) (strInput.charAt(x) ^ cypherBytes[t]) });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 字符数组转换成16进制字符串
	 * @param data 字符数组
	 * @return 转换后的字符串
	 * @author dongxr
	 */
	public String bytesArrToHex(byte[] data) {
		if (data == null) {
			return null;
		}
		int len = data.length;
		String str = "";
		for (int i = 0; i < len; i++) {
			if ((data[i] & 0xFF) < 16)
				str = str + "0" + Integer.toHexString(data[i] & 0xFF);
			else
				str = str + Integer.toHexString(data[i] & 0xFF);
		}
		return str;
	}

	/**
	 * 把字符串加密成字节流的形式
	 * @param keys 加密密钥
	 * @param strInput 需要加密的字符串
	 * @return 加密后的字节流
	 * @author dongxr
	 */
	public String encryptToStream(String keys, String strInput) {
		char[] keyBytes = new char[256];
		char[] cypherBytes = new char[256];
		for (int i = 0; i < 256; ++i) {
			keyBytes[i] = keys.charAt(i % keys.length());
			cypherBytes[i] = (char) i;
		}
		int jump = 0;
		for (int i = 0; i < 256; ++i) {
			jump = (jump + cypherBytes[i] + keyBytes[i]) & 0xFF;
			char tmp = cypherBytes[i];
			cypherBytes[i] = cypherBytes[jump];
			cypherBytes[jump] = tmp;
		}
		int i = 0;
		jump = 0;
		String result = "";
		StringBuilder builder = new StringBuilder();
		for (int x = 0; x < strInput.length(); ++x) {
			i = (i + 1) & 0xFF;
			char tmp = cypherBytes[i];
			jump = (jump + tmp) & 0xFF;
			char t = (char) ((tmp + cypherBytes[jump]) & 0xFF);
			cypherBytes[i] = cypherBytes[jump];
			cypherBytes[jump] = tmp;
			try {
				result += new String(
						new char[] { (char) (strInput.charAt(x) ^ cypherBytes[t])});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 对字符串随机增加长度并加密
	 * @param strInput 需要处理的字符串
	 * @return 处理后的字符串
	 * @author dongxr
	 */
	public String encodeChangeLength(String strInput) {
		//先随机增加字符串的长度
		int length = strInput.length();
		if (length < 24) {
			Random tool = new Random();
			int ranInt = tool.nextInt(24 - length) + 1;
			for (int i = 0; i < ranInt; i++) {
				int temp = tool.nextInt(26) + 97;
				strInput += (char)temp;
			}
			strInput += (char)(ranInt + 97);
		} else {
			strInput += (char)97;
		}
		//对增加长度后的字符串进行流加密
		//return encryptToStream(key, strInput);
		return encryptToArray(key, strInput);
	}

	/**
	 * 对随机增加长度并加密后的字符串解密
	 * @param strInput 需要处理的字符串
	 * @return 处理后的字符串
	 * @author dongxr
	 */
	public String decodeChangeLength(String strInput) {
		if (strInput.length() == 0) {
			return strInput;
		}
		try {
			String[] encodeArr = strInput.split(",");
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < encodeArr.length; i++) {
				if (i == 0) {
					//去掉第一个字符
					builder.append((char)(Integer.valueOf(encodeArr[i].trim().substring(1,encodeArr[i].trim().length())) + 0));
				} else if (i == encodeArr.length - 1) {
					//去掉最后一个字符
					builder.append((char)(Integer.valueOf(encodeArr[i].trim().substring(0,encodeArr[i].trim().length() - 1)) + 0));
				} else {
					builder.append((char)(Integer.valueOf(encodeArr[i].trim()) + 0));
				}
			}
//			System.out.println(builder.toString());
			strInput = builder.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		strInput = encryptToStream(key,strInput);
		//截取出原来的字符串
		return strInput.substring(0, strInput.length() - ((strInput.charAt(strInput.length() - 1)) - 96));
	}
	
	
	public String encryptToArray(String keys,String strInput){
		int[] resultArr = new int[strInput.length()];
		char[] keyBytes = new char[256];
		char[] cypherBytes = new char[256];
		for (int i = 0; i < 256; ++i) {
			keyBytes[i] = keys.charAt(i % keys.length());
			cypherBytes[i] = (char) i;
		}
		int jump = 0;
		for (int i = 0; i < 256; ++i) {
			jump = (jump + cypherBytes[i] + keyBytes[i]) & 0xFF;
			char tmp = cypherBytes[i];
			cypherBytes[i] = cypherBytes[jump];
			cypherBytes[jump] = tmp;
		}
		int i = 0;
		jump = 0;
		for (int x = 0; x < strInput.length(); ++x) {
			i = (i + 1) & 0xFF;
			char tmp = cypherBytes[i];
			jump = (jump + tmp) & 0xFF;
			char t = (char) ((tmp + cypherBytes[jump]) & 0xFF);
			cypherBytes[i] = cypherBytes[jump];
			cypherBytes[jump] = tmp;
			try {
				resultArr[x] = strInput.charAt(x) ^ cypherBytes[t];
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return Arrays.toString(resultArr);
	}
}
