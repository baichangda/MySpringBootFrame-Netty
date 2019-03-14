package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.base.util.DateZoneUtil;
import com.bcd.nettyserver.tcp.parse.FieldParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


public class DateFieldParser implements FieldParser<Date>{
    Logger logger= LoggerFactory.getLogger(DateFieldParser.class);
    public final static DateFieldParser INSTANCE=new DateFieldParser();
    @Override
    public Date parse(byte[] data,Object ...ext) {
        if(data.length==6){
            StringBuilder sb=new StringBuilder("20");
            for (byte b : data) {
                if(b<10){
                    sb.append(0);
                }
                sb.append(b);
            }
            return DateZoneUtil.stringToDate(sb.toString(),"yyyyMMddHHmmss");
        }else{
            return null;
        }
    }
}
