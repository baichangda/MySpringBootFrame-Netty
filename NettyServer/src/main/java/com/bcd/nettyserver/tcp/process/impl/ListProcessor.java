package com.bcd.nettyserver.tcp.process.impl;

import com.bcd.nettyserver.tcp.process.FieldProcessContext;
import com.bcd.nettyserver.tcp.process.FieldProcessor;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class ListProcessor extends FieldProcessor<List> {
    @Override
    public boolean support(Class clazz) {
        return clazz==List.class;
    }

    @Override
    public List process(ByteBuf data, Object instance, FieldProcessContext processContext) {
        int listLen=processContext.getListLen();
        List list=new ArrayList(listLen);
        for (int i = 0; i < listLen; i++) {
            list.add(processor.process(processContext.getFieldInfo().getClazz(),data,0));
        }
        return list;
    }

    @Override
    public void deProcess(List data, ByteBuf dest, FieldProcessContext processContext) {
        for (Object o : data) {
            processor.deProcess(o,dest);
        }
    }
}
