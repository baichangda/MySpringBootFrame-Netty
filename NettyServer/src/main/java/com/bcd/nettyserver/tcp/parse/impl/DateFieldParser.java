package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.base.exception.BaseRuntimeException;
import com.bcd.base.util.DateZoneUtil;
import com.bcd.nettyserver.tcp.parse.FieldParser;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.DateFormatter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class DateFieldParser implements FieldParser<Date>{
    Logger logger= LoggerFactory.getLogger(DateFieldParser.class);
    public final static DateFieldParser INSTANCE=new DateFieldParser();
    @Override
    public Date parse(ByteBuf data,int len, Object ...ext) {
        if(len==6){
            byte year=data.readByte();
            int month=data.readByte();
            int day=data.readByte();
            int hour=data.readByte();
            int minute=data.readByte();
            int second=data.readByte();
            return Date.from(LocalDateTime.of(2000+year,month,day,hour,minute,second).toInstant(DateZoneUtil.ZONE_OFFSET));
        }else{
            throw BaseRuntimeException.getException("date length must be 6,actual "+len);
        }
    }
}
