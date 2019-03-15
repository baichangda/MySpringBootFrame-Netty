package com.bcd.nettyserver.tcp.util;

@SuppressWarnings("unchecked")
public class ParseUtil {


    public static String toHex(byte[] content){
        StringBuilder hex=new StringBuilder();
        for (byte b : content) {
            String s=Integer.toHexString(b&0xff);
            if(s.length()==1){
                hex.append("0");
            }
            hex.append(s);
        }
        return hex.toString();
    }
}
