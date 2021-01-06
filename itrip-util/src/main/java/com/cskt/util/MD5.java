package com.cskt.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * @description: MD5
 * @author: Mr.阿九
 * @createTime: 2021-01-06 11:57
 **/
public class MD5 {
    /**
     *
     * @param plainText 加密的字符串
     * @param length 加密完成后的长度
     * @return
     */
    public static String getMd5(String plainText, int length) {
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
        // 32位
        // return buf.toString();
        // 16位
        // return buf.toString().substring(0, 16);
            return buf.toString().substring(0, length);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static int getRandomCode() {
        int max = 9999;
        int min = 1111;
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

}
