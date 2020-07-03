package com.bcd.nettyserver.tcp.process;


import io.netty.buffer.ByteBuf;

public abstract class FieldProcessor<T> {
    protected Processor processor;

    public void setProcessor(Processor processor){
        this.processor=processor;
    }

    public Processor getProcessor() {
        return processor;
    }

    /**
     * 当前字段解析器是否支持
     * @param clazz
     * @return
     */
    public abstract boolean support(Class clazz);

    /**
     * 读取byteBuf数据转换成对象
     * @param data
     * @param instance
     * @param processContext
     * @return
     */
    public abstract T process(ByteBuf data, Object instance, FieldProcessContext processContext);

    /**
     * 解析对象转换为byteBuf
     * @param data
     * @param dest
     * @param processContext
     */
    public abstract void deProcess(T data, ByteBuf dest, FieldProcessContext processContext);

}
