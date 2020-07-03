package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.base.exception.BaseRuntimeException;
import com.bcd.base.util.DateZoneUtil;
import com.bcd.nettyserver.tcp.parse.FieldParseContext;
import com.bcd.nettyserver.tcp.parse.FieldParser;
import com.bcd.nettyserver.tcp.parse.FieldToByteBufContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Date;


public class DateFieldParser implements FieldParser<Date> {
    public final static int BASE_YEAR=2000;

    Logger logger= LoggerFactory.getLogger(DateFieldParser.class);
    @Override
    public Date parse(ByteBuf data,int len, FieldParseContext context) {
        if(len==6){
            byte year=data.readByte();
            int month=data.readByte();
            int day=data.readByte();
            int hour=data.readByte();
            int minute=data.readByte();
            int second=data.readByte();
            return Date.from(LocalDateTime.of(BASE_YEAR+year,month,day,hour,minute,second).toInstant(DateZoneUtil.ZONE_OFFSET));
        }else{
            throw BaseRuntimeException.getException("date length must be 6,actual "+len);
        }
    }

    @Override
    public void toByteBuf(Date data, int len, FieldToByteBufContext context,ByteBuf result) {
        checkByteBufData(data);
        if(len==6){
            LocalDateTime ldt= LocalDateTime.ofInstant(data.toInstant(), DateZoneUtil.ZONE_ID);
            result.writeByte(ldt.getYear()-BASE_YEAR);
            result.writeByte(ldt.getMonthValue());
            result.writeByte(ldt.getDayOfMonth());
            result.writeByte(ldt.getHour());
            result.writeByte(ldt.getMinute());
            result.writeByte(ldt.getSecond());
        }else{
            throw BaseRuntimeException.getException("date length must be 6,actual "+len);
        }
    }
}
