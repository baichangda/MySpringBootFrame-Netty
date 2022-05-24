package com.bcd.protocol.gb32960.datahandler;

import com.bcd.base.exception.BaseRuntimeException;
import com.bcd.protocol.gb32960.define.CommonConst;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DataHandler<T> {
    protected Logger logger= LoggerFactory.getLogger(this.getClass());

    private int flag;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public DataHandler(int flag) {
        this.flag=flag;
        if(CommonConst.DATA_HANDLER_ARR[flag]==null){
            CommonConst.DATA_HANDLER_ARR[flag]=this;
        }else{
            throw BaseRuntimeException.getException("Construct DataHandler["+this.getClass().getName()+"] With Flag["+flag+"] Failed,Already Has DataHandler["+CommonConst.DATA_HANDLER_ARR[flag].getClass().getName()+"]");
        }
    }

    public abstract void handle(T data,ChannelHandlerContext ctx);
}
