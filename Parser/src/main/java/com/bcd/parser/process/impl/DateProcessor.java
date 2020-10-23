package com.bcd.parser.process.impl;

import com.bcd.base.exception.BaseRuntimeException;
import com.bcd.base.util.DateZoneUtil;
import com.bcd.parser.info.FieldInfo;
import com.bcd.parser.process.FieldDeProcessContext;
import com.bcd.parser.process.FieldProcessContext;
import com.bcd.parser.process.FieldProcessor;
import io.netty.buffer.ByteBuf;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

public class DateProcessor extends FieldProcessor<Date> {
    private final static int BASE_YEAR=2000;

    @Override
    public Date process(ByteBuf data, FieldProcessContext processContext){
        if(processContext.getLen()==6){
            byte year=data.readByte();
            int month=data.readByte();
            int day=data.readByte();
            int hour=data.readByte();
            int minute=data.readByte();
            int second=data.readByte();
            return Date.from(LocalDateTime.of(BASE_YEAR+year,month,day,hour,minute,second).toInstant(DateZoneUtil.ZONE_OFFSET));
        }else{
            throw BaseRuntimeException.getException("date length must be 6,actual "+processContext.getLen());
        }
    }

    @Override
    public void deProcess(Date data, ByteBuf dest, FieldDeProcessContext processContext) {
        Objects.requireNonNull(data);
        if(processContext.getLen()==6){
            LocalDateTime ldt= LocalDateTime.ofInstant(data.toInstant(), DateZoneUtil.ZONE_ID);
            dest.writeByte(ldt.getYear()-BASE_YEAR);
            dest.writeByte(ldt.getMonthValue());
            dest.writeByte(ldt.getDayOfMonth());
            dest.writeByte(ldt.getHour());
            dest.writeByte(ldt.getMinute());
            dest.writeByte(ldt.getSecond());
        }else{
            throw BaseRuntimeException.getException("date length must be 6,actual "+processContext.getLen());
        }
    }
}
