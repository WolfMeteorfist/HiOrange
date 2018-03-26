package com.yuanshi.hiorange;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yuanshi.hiorange.util.Codec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public class BoxTest {
	private static Gson gson = new Gson();
//	private static String url = "http://54.222.203.152:9090/plugins/JDBProcess/dm";
//	private static String url = "http://52.80.97.230:9090/plugins/SmartSwitch/sev";
//	private static String url = "http://52.80.97.230:9090/plugins/FiveFunctionAndroid/five";
	private static String url = "http://54.222.203.152:9090/plugins/SmartBox/sev";
	
//	private static String url = "http://localhost:9090/plugins/JDBProcess/dm";
	private static String clientUrl = "http://54.222.203.152:9090/plugins/SmartBox/sevc";
//	final static String TOKEN = "3a98c51c-f7eb-4d68-87db-c7d9fe04f78d";  // 登录获取token后手动更新,方便测试
	final static String TOKEN = "277858f7-d20d-4216-90c2-eb196c7a8beb";  // local 登录获取token后手动更新,方便测试
	final static String appUser = "15818724679";
	
	
	

	public static void main(String[] args) throws Exception {
//		 app注册
//		regeditForApp();
		// 登录
//		loginForApp();
		// 重置密码
//		resetPwd();
		// 绑定箱子
		bindBox();
		// 解绑箱子
//		unbindBox();
		// 获取箱子信息
		query();
		// 获取箱子GPS信息
//		queryGps();
		// 设置箱子信息
//		setCommand();

		// 客户端注册
//		regeditForClient();
	}
	
	
	/**
	 * 客户端注册
	 */
	private static void regeditForClient() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"act\":\"box_reg_client\",");
		sb.append("\"box_id\":\"4444\"");
		sb.append("}");
		System.out.println(sb.toString());
		sendPost(clientUrl, sb.toString());
	}


	/**
	 * 设置箱子信息
	 */
	private static void setCommand() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"act\":\"set\",");
		sb.append("\"phone\":\"15818724679\",");
		sb.append("\"box_id\":\"15818724679\",");
		sb.append("\"COMMAND\":\"t43t2543t54\",");
		String tempStr = sb.toString()+"\"sign\":\"\"}";
		sb.append("\"sign\":\""+getSign(tempStr)+"\"");
		sb.append("}");
		System.out.println(sb.toString());
		sendPost(url, Codec.codec(sb.toString()));
	}


	/**
	 * 获取箱子gps信息
	 */
	private static void queryGps() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"act\":\"query_gps\",");
		sb.append("\"phone\":\"15818724679\",");
		sb.append("\"box_id\":\"3333\",");
		String tempStr = sb.toString()+"\"sign\":\"\"}";
		sb.append("\"sign\":\""+getSign(tempStr)+"\"");
		sb.append("}");
		System.out.println(sb.toString());
		sendPost(url, Codec.codec(sb.toString()));
	}



	/**
	 * 获取箱子信息
	 */
	private static void query() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"act\":\"query\",");
		sb.append("\"phone\":\"15818724679\",");
		sb.append("\"box_id\":\"3333\",");
		String tempStr = sb.toString()+"\"sign\":\"\"}";
		sb.append("\"sign\":\""+getSign(tempStr)+"\"");
		sb.append("}");
		System.out.println(sb.toString());
		sendPost(url, Codec.codec(sb.toString()));
	}



	/**
	 * 解绑箱子
	 */
	private static void unbindBox() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"act\":\"unbind\",");
//		sb.append("\"phone\":\"15818724679\",");
		sb.append("\"phone\":\"15818724679\",");
		sb.append("\"box_id\":\"3333\",");
		String tempStr = sb.toString()+"\"sign\":\"\"}";
		sb.append("\"sign\":\""+getSign(tempStr)+"\"");
		sb.append("}");
		System.out.println(sb.toString());
		sendPost(url, Codec.codec(sb.toString()));
	}



	/**
	 * 绑定箱子
	 */
	private static void bindBox() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"act\":\"bind\",");
		sb.append("\"phone\":\"000\",");
		sb.append("\"box_id\":\"356269020396835\",");
		String tempStr = sb.toString()+"\"sign\":\"\"}";
		sb.append("\"sign\":\""+getSign(tempStr)+"\"");
		sb.append("}");
		System.out.println(sb.toString());
		sendPost(url, Codec.codec(sb.toString()));
	}



	/**
	 * 重置密码
	 */
	private static void resetPwd() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"act\":\"reset_password\",");
		sb.append("\"user\":{");
		sb.append("\"phone\":\"15818724679\",");
		sb.append("\"password\":\"3213212321\"");
		sb.append("},");
		sb.append("\"verificationCode\":{");
		sb.append("\"value\":\"22\"");
		sb.append("},");
		String tempStr = sb.toString()+"\"sign\":\"\"}";
		sb.append("\"sign\":\""+getSign(tempStr)+"\"");
		sb.append("}");
		System.out.println(sb.toString());
		sendPost(url, Codec.codec(sb.toString()));
	}




	/**
	 * 登录
	 */
	private static void loginForApp() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"act\":\"login\",");
		sb.append("\"user\":{");
		sb.append("\"phone\":\"15818724679\",");
		sb.append("\"password\":\"32132122\"");
		sb.append("},");
		sb.append("\"version\":\"1.0\",");
		String tempStr = sb.toString()+"\"sign\":\"\"}";
		sb.append("\"sign\":\""+getSign(tempStr)+"\"");
		sb.append("}");
		System.out.println(sb.toString());
		sendPost(url, Codec.codec(sb.toString()));
	}




	/**
	 * app注册
	 */
	private static void regeditForApp() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"act\":\"register\",");
		sb.append("\"user\":{");
		sb.append("\"phone\":\"15818724679\",");
		sb.append("\"password\":\"321321\"");
		sb.append("},");
		sb.append("\"verificationCode\":{");
		sb.append("\"value\":\"22\"");
		sb.append("},");
		String tempStr = sb.toString()+"\"sign\":\"\"}";
		sb.append("\"sign\":\""+getSign(tempStr)+"\"");
		sb.append("}");
		System.out.println(sb.toString());
		sendPost(url, Codec.codec(sb.toString()));
	}


class gpslist{
	private String lat;
	private String lon;
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	
}
    
	private static String getSig(String url)throws Exception{
		// 通过steam先拿到发过来的POST数据
		String sInput = url;
		Gson gson = new Gson();
		Map<String, Object> json = new HashMap<String, Object>();
		
		try {
			json = gson.fromJson(sInput, Map.class);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
        	return null;
		}
		
        
        // 校验sign
        List<String> keyList = new ArrayList<String>();
        for (String key : json.keySet()) {
            if ("sig".equals(key)) {
                continue;
            }
            keyList.add(key);
        }
        
        Collections.sort(keyList);
        StringBuffer sb = new StringBuffer();
        for (String key : keyList) {
        	 String v = json.get(key).toString();
             sb.append(v);

        }
        sb.append("8ee6c3df79047d559645b781d3a3c6af");
        String sign2 = getMd5(sb.toString());
        
        return sign2;
	}
	
	/**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            String finalParam = new String(param.getBytes(),"utf-8");
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            System.out.println("result:===================");
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }    
    
    
    
	/**
	 * sign
	 * @return
	 */
	private static String getSign(String jsonTmp){
		// 通过steam先拿到发过来的POST数据
		String sInput = jsonTmp;
		
		
		Gson gson = new Gson();
		Map<String, Object> json = new HashMap<String, Object>();
		
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
        StringBuffer sb = new StringBuffer();
        for (String key : keyList) {
        	 String v = json.get(key).toString();
             sb.append(v);

        }
        sb.append("sBoxApp");
        System.out.println("sb:"+sb.toString());
        String sign2 = getMd5(sb.toString());
        System.out.println("sign:"+sign2);
        return sign2;
	}
	
	/**
	 * 获取MD5 32位
	 * @param plainText
	 * @return
	 */
	public static String getMd5(String plainText) {  
        try {  
            MessageDigest md = MessageDigest.getInstance("MD5");  
            md.update(plainText.getBytes());  
            byte b[] = md.digest();  
  
            int i;  
  
            StringBuffer buf = new StringBuffer("");  
            for (int offset = 0; offset < b.length; offset++) {  
                i = b[offset];  
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
}
