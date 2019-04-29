package com.ming.seckill.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    public static String md5(String str){
        return DigestUtils.md5Hex(str);
    }

    private static final String salt = "1a2b3c4d";

    public static String inputPassToFormPass(String inputPass){
        String str = ""+salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(4) + salt.charAt(5);
        return md5(str);
    }

    public static String formPassToDBPass(String formPass,String salt){
        String str = ""+salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(4) + salt.charAt(5);
        return md5(str);
    }

    public static String inputPassToDBPass(String inputPass,String salt){
        return formPassToDBPass(inputPassToFormPass(inputPass),salt);
    }

    public static void main(String[] args) {
        System.out.println(inputPassToFormPass("123456"));//f2266f92e31d032983464ea05d6fb4e4
        //7516c04c34c57db8595b58e49e84e637
        System.out.println(inputPassToDBPass("123456","1a2b3c4d"));
    }
}
