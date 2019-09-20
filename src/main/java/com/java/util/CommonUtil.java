package com.java.util;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.conn.db.base.entity.Constants;

public class CommonUtil {

	public static void main(String arg[]) {
		String ss="huasheng.liang";
		//编码
		System.out.println(encode(ss.getBytes()));
		//解码
		System.out.println(decode(encode(ss.getBytes()).getBytes()));
	}
	
	public Map<Integer, String> getKeyPair(){
	Map<Integer, String> keyMap=new HashMap<Integer, String>();
	try {
		//1.生成公钥和私钥对，基于RSA算法生成对象
		KeyPairGenerator keyPairGen=KeyPairGenerator.getInstance("RSA");
		//2.初始化密钥对生成器
		keyPairGen.initialize(1024, new SecureRandom());
		//3.生成一个密钥对，https://blog.csdn.net/qy20115549/article/details/83105736
		KeyPair keypair=keyPairGen.generateKeyPair();
	} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return keyMap;
	}
	
	
	/**
	 * base64编码
	 **/
	public static String encode(byte[] bytes) {
		return new String(Base64.encodeBase64(bytes));
	}
	/**
	 * base64解码
	 */
	public static String decode(byte[] bytes) {
		return new String(Base64.decodeBase64(bytes));
	}
	
	/**
	 * MD5加密
	 **/

	
	public static String cryptionMD5(String inStr){
		MessageDigest md5=null;
		try {
			md5=MessageDigest.getInstance("MD5");
			//将加密明文转化成字节数组
			char[] charArr=inStr.toCharArray();
			byte[] byteArr=new byte[charArr.length];
			for(int i=0;i<charArr.length;i++) {
				byteArr[i]=(byte) charArr[i];
			}
			//获取经MD5加密后的字节码
			byte[] md5Bytes=md5.digest(byteArr);
			//转成16进制
			StringBuffer hexvalue=new StringBuffer();
			for(int i=0;i<md5Bytes.length;i++) {
				int val=((int)md5Bytes[i])&0xff;
				if(val<16) 
					hexvalue.append("0");
				hexvalue.append(Integer.toHexString(val));	
			}
			return hexvalue.toString();
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	
	/**
	 * 将MD5加密的字符串和密钥进行解密
	 * @param data -需要解密的数据
	 * @param secret -密钥
	 * @return byte[] -返回字节数组(二进制数组)
	 * @throws NoSuchAlgorithmException 
	 * */
	public static byte[] encryptionMD5(String data,String secret)  {
		byte[] bytes=null;
		String tmpStr=data+secret;
		try {
			MessageDigest md5=MessageDigest.getInstance("MD5");
			return md5.digest(tmpStr.getBytes(Constants.ENCODE));
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return bytes;
	}
}
