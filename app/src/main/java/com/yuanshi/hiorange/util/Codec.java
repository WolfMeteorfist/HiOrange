package com.yuanshi.hiorange.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Codec{

	static char [] tab_enc = "nlmopqQH78SkC012xyMzw9FOPGRTUfghijrI#,=AstKL3de4u6J5Dv+/*&WXYVaBNbcZE".toCharArray();
	static char [] tab_dec = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/*&#,=".toCharArray();

	public static String codec(String content){
		try {
			System.out.println(tab_dec.length);
			System.out.println(tab_enc.length);
			String v = Base64.encode(content.getBytes("UTF-8"));
			char [] src = v.toCharArray();
			StringBuffer sb = new StringBuffer();
			for(int j=0; j<src.length; j++){
				for(int i=0; i<tab_dec.length; i++){
					if(src[j]==tab_dec[i]){
						sb.append(tab_enc[i]);
					}
				}
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}
	
	public static String decode(String input){
		StringBuffer sb = new StringBuffer();
		char [] src = input.toCharArray();
		for(int j=0; j<src.length; j++){
			for(int i=0; i<tab_enc.length; i++){
				if(src[j]==tab_enc[i]){
					sb.append(tab_dec[i]);
				}
			}
		}
		String value = sb.toString();
		byte [] bArray = Base64.decode(value);
		try {
			return new String(bArray, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			
		}
		return null;
	}

	/**
	 * 获取MD5 32位
	 * @param plainText
	 * @return
	 */
	private static String getMd5(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (byte bit : b) {
				i = bit;
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					buf.append("0");
				}
				buf.append(Integer.toHexString(i));
			}
			//32位加密
			return buf.toString().toLowerCase();
			// 16位的加密
			//return buf.toString().substring(8, 24);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * sign
	 *
	 * @return
	 */
	public static String getSign(String jsonTmp) {
		// 通过steam先拿到发过来的POST数据
		String sInput = jsonTmp;


		Gson gson = new Gson();
		Map<String, Object> json;

		try {
			json = gson.fromJson(sInput, Map.class);
		} catch (JsonSyntaxException e) {
			return null;
		}


		// 校验sign
		List<String> keyList = new ArrayList<String>();
		for (String key : json.keySet()) {
			if ("sign".equals(key)) {
				continue;
			}
			keyList.add(key);
		}

		Collections.sort(keyList);
		StringBuilder sb = new StringBuilder();
		for (String key : keyList) {
			String v = json.get(key).toString();
			sb.append(v);

		}
		sb.append("sBoxApp");
		System.out.println("sb:" + sb.toString());
		String sign2 = getMd5(sb.toString());
		System.out.println("sign:" + sign2);
		return sign2;
	}

}
