package com.bcd.parser.process;


import com.bcd.base.exception.BaseRuntimeException;
import com.bcd.base.util.RpnUtil;
import com.bcd.parser.Parser;
import com.bcd.parser.info.FieldInfo;
import io.netty.buffer.ByteBuf;

import java.lang.reflect.Field;
import java.util.Objects;

public abstract class FieldProcessor<T> {
    protected Parser parser;

    public void setParser(Parser parser){
        this.parser = parser;
    }

    public Parser getParser() {
        return parser;
    }

    /**
     * 读取byteBuf数据转换成对象
     * @param data
     * @param processContext
     * @return
     */
    public abstract T process(ByteBuf data, FieldProcessContext processContext);

    /**
     * 解析对象转换为byteBuf
     * @param data
     * @param dest
     * @param processContext
     */
    public abstract void deProcess(T data, ByteBuf dest, FieldDeProcessContext processContext);

    protected double withValExpr(double val,FieldProcessContext processContext){
        Object[] valRpn= processContext.getFieldInfo().getValRpn();
        if(valRpn==null){
            return val;
        }else {
            return RpnUtil.calcRPN_char_double_singleVar(valRpn, val);
        }
    }

    protected void checkValRpnNull(FieldDeProcessContext processContext){
        if(processContext.getFieldInfo().getValRpn()!=null){
            Field field=processContext.getFieldInfo().getField();
            throw BaseRuntimeException.getException("class[{0}] field[{1}] has valRpn,deProcess not support",
                    field.getDeclaringClass().getName(),
                    field.getName());
        }
    }

}
