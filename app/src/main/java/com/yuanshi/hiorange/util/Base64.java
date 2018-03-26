package com.yuanshi.hiorange.util;

import java.io.UnsupportedEncodingException;

/**
 * Base64编码与解码
 * <p>
 * 来源于互联网</p>
 *
 * @author ybbk
 * @date 2010.07.29
 *
 */
public class Base64 {

    public static final int URL_SAFE = 0x01;

    public static String encode(byte[] data) {
        return encode(data, 0);
    }

    // 编码传入的字节数组，输出编码后的字符数组
    public static String encode(byte[] data, int flag) {
        char[] encode = alphabet;
        if ((flag & URL_SAFE) == URL_SAFE) {
            encode = alphabet_url;
        }
        char[] out = new char[((data.length + 2) / 3) * 4];

        // 对字节进行Base64编码,每三个字节转化为4个字符.
        // 输出总是能被4整除的偶数个字符
        //
        for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
            boolean quad = false;
            boolean trip = false;
            int val = (0xFF & (int) data[i]);
            val <<= 8;
            if ((i + 1) < data.length) {
                val |= (0xFF & (int) data[i + 1]);
                trip = true;
            }
            val <<= 8;
            if ((i + 2) < data.length) {
                val |= (0xFF & (int) data[i + 2]);
                quad = true;
            }
            out[index + 3] = encode[(quad ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 2] = encode[(trip ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 1] = encode[val & 0x3F];
            val >>= 6;
            out[index + 0] = encode[val & 0x3F];
        }
        return new String(out);
    }

    public static byte[] decode(String inStr) {
        return decode(inStr, 0);
    }

    public static byte[] decode(String inStr, int flag) {
        byte[] decode = codes;
        if ((flag & URL_SAFE) == URL_SAFE) {
            decode = codes_url;
        }

        // 程序中有判断如果有回车、空格等非法字符，则要去掉这些字符
        // 这样就不会计算错误输出的内容
        char[] data = inStr.toCharArray();
        int tempLen = data.length;
        for (int ix = 0; ix < data.length; ix++) {
            if ((data[ix] > 255) || decode[data[ix]] < 0) {
                --tempLen; // 去除无效的字符
            }
        }
        // 计算byte的长度
        // -- 每四个有效字符输出三个字节的内容
        // -- 如果有额外的3个字符，则还要加上2个字节,
        // 或者如果有额外的2个字符，则要加上1个字节
        int len = (tempLen / 4) * 3;
        if ((tempLen % 4) == 3) {
            len += 2;
        }
        if ((tempLen % 4) == 2) {
            len += 1;
        }
        byte[] out = new byte[len];
        int shift = 0;
        int accum = 0;
        int index = 0;
        // 一个一个字符地解码（注意用的不是tempLen的值进行循环）
        for (int ix = 0; ix < data.length; ix++) {
            int value = (data[ix] > 255) ? -1 : decode[data[ix]];
            if (value >= 0) { // 忽略无效字符
                accum <<= 6;
                shift += 6;
                accum |= value;
                if (shift >= 8) {
                    shift -= 8;
                    out[index++] = (byte) ((accum >> shift) & 0xff);
                }
            }
        }
        // 如果数组长度和实际长度不符合，那么抛出错误
        if (index != out.length) {
            throw new Error("数据长度不一致(实际写入了 " + index + "字节，但是系统指示有" + out.length + "字节)");
        }
        return out;
    }

    //
    // 用于编码的字符
    final static private char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
    final static private char[] alphabet_url = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.".toCharArray();
    //
    // 用于解码的字节（0-255）
    //
    final private static byte[] codes = new byte[256];
    final private static byte[] codes_url = new byte[256];

    static {
        for (int i = 0; i < 256; i++) {
            codes[i] = -1;
            codes_url[i] = -1;
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            codes[i] = (byte) (i - 'A');
            codes_url[i] = (byte) (i - 'A');
        }
        for (int i = 'a'; i <= 'z'; i++) {
            codes[i] = (byte) (26 + i - 'a');
            codes_url[i] = (byte) (26 + i - 'a');
        }
        for (int i = '0'; i <= '9'; i++) {
            codes[i] = (byte) (52 + i - '0');
            codes_url[i] = (byte) (52 + i - '0');
        }
        codes['+'] = 62;
        codes['/'] = 63;

        codes_url['-'] = 62;
        codes_url['_'] = 63;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String str = "eyJzaWQiOiIwMDAwIiwgImNpZCI6IjAwMDAifQ";
        byte[] bArray = Base64.decode(str, Base64.URL_SAFE);

        System.out.println(new String(bArray, "UTF-8"));
    }
}
