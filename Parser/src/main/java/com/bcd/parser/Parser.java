package com.bcd.parser;

import com.bcd.base.exception.BaseRuntimeException;
import com.bcd.base.util.ClassUtil;
import com.bcd.base.util.ProxyUtil;
import com.bcd.base.util.RpnUtil;
import com.bcd.base.util.SpringUtil;
import com.bcd.parser.anno.Parsable;
import com.bcd.parser.info.FieldInfo;
import com.bcd.parser.info.OffsetFieldInfo;
import com.bcd.parser.info.PacketInfo;
import com.bcd.parser.process.FieldDeProcessContext;
import com.bcd.parser.process.FieldProcessContext;
import com.bcd.parser.process.FieldProcessor;
import com.bcd.parser.process.impl.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.util.*;


@SuppressWarnings("unchecked")
public abstract class Parser {

    public final Map<Class, PacketInfo> packetInfoCache =new HashMap<>();

    /**
     * 处理器数组
     */
    public FieldProcessor[] fieldProcessors;

    /**
     * 启用偏移字段值处理
     */
    protected boolean enableOffsetField=false;

    /**
     * 基础处理器,内置
     */
    protected List<FieldProcessor> baseProcessorList;
    protected ByteProcessor byteProcessor=new ByteProcessor();
    protected ShortProcessor shortProcessor=new ShortProcessor();
    protected IntegerProcessor integerProcessor=new IntegerProcessor();
    protected LongProcessor longProcessor=new LongProcessor();
    protected ByteArrayProcessor byteArrayProcessor=new ByteArrayProcessor();
    protected ShortArrayProcessor shortArrayProcessor=new ShortArrayProcessor();
    protected IntegerArrayProcessor integerArrayProcessor=new IntegerArrayProcessor();
    protected LongArrayProcessor longArrayProcessor=new LongArrayProcessor();
    protected StringProcessor stringProcessor=new StringProcessor();
    protected DateProcessor dateProcessor=new DateProcessor();
    protected ByteBufProcessor byteBufProcessor=new ByteBufProcessor();
    protected ListProcessor listProcessor=new ListProcessor();

    public void init(){
        //初始化处理器
        initProcessor();

        initPacketInfo();

        afterInit();
    }

    private void afterInit(){
        for (FieldProcessor processor : fieldProcessors) {
            processor.setParser(this);
        }
    }

    private void initProcessor(){
        List<FieldProcessor> processorList=new ArrayList<>();
        baseProcessorList=initBaseProcessor();
        processorList.addAll(baseProcessorList);
        processorList.addAll(initExtProcessor());
        fieldProcessors=processorList.toArray(new FieldProcessor[0]);
    }

    /**
     * 初始化基础处理器
     */
    private List<FieldProcessor> initBaseProcessor(){
        List<FieldProcessor> processorList=new ArrayList<>();
        processorList.add(this.byteProcessor);
        processorList.add(this.shortProcessor);
        processorList.add(this.integerProcessor);
        processorList.add(this.longProcessor);
        processorList.add(this.byteArrayProcessor);
        processorList.add(this.shortArrayProcessor);
        processorList.add(this.integerArrayProcessor);
        processorList.add(this.longArrayProcessor);
        processorList.add(this.stringProcessor);
        processorList.add(this.dateProcessor);
        processorList.add(this.byteBufProcessor);
        processorList.add(this.listProcessor);
        return processorList;
    }

    /**
     * 初始化额外处理器
     * 主要是自定义处理器
     */
    protected List<FieldProcessor> initExtProcessor(){
        return Collections.emptyList();
    }

    /**
     * 初始化 classToHandler
     * 通过spring
     */
    protected List<FieldProcessor> initProcessorBySpring(){
        List<FieldProcessor> processorList=new ArrayList<>();
        SpringUtil.applicationContext.getBeansOfType(FieldProcessor.class).values().forEach(e->{
            Class clazz= ProxyUtil.getSource(e).getClass();
            processorList.add(e);
        });
        return processorList;
    }

    /**
     * 初始化 classToParser
     * 去除接口和抽象类
     * 通过扫描
     */
    protected List<FieldProcessor> initProcessorByScanClass(String pkg){
        List<FieldProcessor> processorList=new ArrayList<>();
        try {
            A:for (Class e : ClassUtil.getClassesByParentClass(FieldProcessor.class, pkg)) {
                for (FieldProcessor base : baseProcessorList) {
                    if(e==base.getClass()){
                        continue A;
                    }
                }
                processorList.add((FieldProcessor) e.newInstance());
            }
        } catch (IOException | ClassNotFoundException |IllegalAccessException |InstantiationException e) {
            throw BaseRuntimeException.getException(e);
        }
        return processorList;
    }

    /**
     * 加载所有 {@link Parsable} 注解的类并转换为 {@link PacketInfo}
     */
    protected abstract void initPacketInfo();

    /**
     * 加载所有 {@link Parsable} 注解的类并转换为 {@link com.bcd.parser.info.PacketInfo}
     * 存储在map中
     * @param packageName
     */
    protected void initPacketInfoByScanClass(String packageName){
        try {
            for (Class e : ClassUtil.getClassesWithAnno(Parsable.class, packageName)) {
                packetInfoCache.put(e, ParserUtil.toPacketInfo(e,enableOffsetField,fieldProcessors));
            }
        } catch (IOException | ClassNotFoundException e) {
            throw BaseRuntimeException.getException(e);
        }
    }

    public final <T>T parse(Class<T> clazz, ByteBuf data){
        return parse(clazz,data,null);
    }


    /**
     * 根据类型和缓冲数据生成对应对象
     * 所有涉及解析对象必须有空参数的构造方法
     * @param clazz
     * @param data
     * @param parentContext 当前对象作为其他类的字段解析时候 的环境
     * @param <T>
     * @return
     */
    public final <T>T parse(Class<T> clazz, ByteBuf data, FieldProcessContext parentContext){
        //解析包
        PacketInfo packetInfo=packetInfoCache.get(clazz);
//        if(packetInfo==null){
//            throw BaseRuntimeException.getException("can not find class["+clazz.getName()+"] packetInfo");
//        }
        try {
            //构造实例
            T instance= clazz.newInstance();
            //进行解析
            int varValArrLen=packetInfo.getVarValArrLen();
            int varValArrOffset=packetInfo.getVarValArrOffset();
            int [] vals;
            if(varValArrLen!=0){
                vals=new int[varValArrLen];
            }else{
                vals=null;
            }
            FieldProcessContext processContext=new FieldProcessContext();
            processContext.setParentContext(parentContext);
            processContext.setInstance(instance);
            for (FieldInfo fieldInfo : packetInfo.getFieldInfoList()) {
                int processorIndex=fieldInfo.getProcessorIndex();
                /**
                 * 代表 {@link PacketField#lenExpr()}
                 */
                Object[] lenRpn= fieldInfo.getLenRpn();
                /**
                 * 代表 {@link PacketField#listLenExpr()}
                 */
                Object[] lisLlenRpn= fieldInfo.getListLenRpn();
                int len;
                int listLen=0;
                if(lenRpn==null){
                    len=fieldInfo.getPacketField_len();
                }else{
                    if(lenRpn.length==1){
                        len=vals[(char)lenRpn[0]-varValArrOffset];
                    }else {
                        len = RpnUtil.calcRPN_char_int(lenRpn, vals,varValArrOffset);
                    }
                }
                if(lisLlenRpn!=null){
                    if(lisLlenRpn.length==1){
                        listLen=vals[(char)lisLlenRpn[0]-varValArrOffset];
                    }else {
                        listLen = RpnUtil.calcRPN_char_int(lisLlenRpn, vals,varValArrOffset);
                    }
                }
                processContext.setFieldInfo(fieldInfo);
                processContext.setLen(len);
                processContext.setListLen(listLen);
                Object val=fieldProcessors[processorIndex].process(data,processContext);
                if(fieldInfo.isVar()){
                    vals[fieldInfo.getPacketField_var()-varValArrOffset]=((Number)val).intValue();
                }
                fieldInfo.getField().set(instance,val);
            }

            if(enableOffsetField) {
                //偏移量值计算
                List<OffsetFieldInfo> offsetFieldInfoList = packetInfo.getOffsetFieldInfoList();
                if (offsetFieldInfoList != null && !offsetFieldInfoList.isEmpty()) {
                    Map<String, Double> map = new HashMap<>();
                    //define temp var
                    Object sourceVal;
                    double destVal;
                    int fieldType;
                    for (OffsetFieldInfo offsetFieldInfo : offsetFieldInfoList) {
                        fieldType=offsetFieldInfo.getFieldType();
                        sourceVal = offsetFieldInfo.getSourceField().get(instance);
                        destVal = RpnUtil.calcRPN_char_double_singleVar(offsetFieldInfo.getRpn(),((Number) sourceVal).doubleValue());
                        switch (fieldType) {
                            case 1: {
                                offsetFieldInfo.getField().set(instance, (byte)destVal);
                                break;
                            }
                            case 2: {
                                offsetFieldInfo.getField().set(instance, (short)destVal);
                                break;
                            }
                            case 3: {
                                offsetFieldInfo.getField().set(instance, (int)destVal);
                                break;
                            }
                            case 4: {
                                offsetFieldInfo.getField().set(instance, (long)destVal);
                                break;
                            }
                            case 5: {
                                offsetFieldInfo.getField().set(instance, (float)destVal);
                                break;
                            }
                            case 6: {
                                offsetFieldInfo.getField().set(instance, destVal);
                                break;
                            }
                        }
                    }
                }
            }

            return instance;
        } catch (InstantiationException |IllegalAccessException e) {
            throw BaseRuntimeException.getException(e);
        }
    }


    public final void deParse(Object t, ByteBuf res){
        deParse(t, res,null);
    }

    /**
     * 将对象转换为byteBuf
     * @param t 不能为null
     * @param res
     * @param parentContext
     */
    public final void deParse(Object t, ByteBuf res, FieldDeProcessContext parentContext){
        try{
            if(res==null){
                res= Unpooled.buffer();
            }
            Class clazz= t.getClass();
            PacketInfo packetInfo=packetInfoCache.get(clazz);
            //进行解析
            int [] vals=null;
            int varValArrLen=packetInfo.getVarValArrLen();
            int varValArrOffset=packetInfo.getVarValArrOffset();
            if(varValArrLen!=0){
                vals=new int[varValArrLen];
            }
            FieldDeProcessContext processContext=new FieldDeProcessContext();
            processContext.setInstance(t);
            processContext.setParentContext(parentContext);
            for (FieldInfo fieldInfo : packetInfo.getFieldInfoList()) {
                int processorIndex=fieldInfo.getProcessorIndex();
                Object data=fieldInfo.getField().get(t);
                /**
                 * 代表 {@link PacketField#lenExpr()}
                 */
                Object[] lenRpn= fieldInfo.getLenRpn();
                /**
                 * 代表 {@link PacketField#listLenExpr()}
                 */
                Object[] lisLlenRpn= fieldInfo.getListLenRpn();
                int len;
                int listLen=0;
                if(lenRpn==null){
                    len=fieldInfo.getPacketField_len();
                }else{
                    if(lenRpn.length==1){
                        len=vals[(char)lenRpn[0]-varValArrOffset];
                    }else {
                        len = RpnUtil.calcRPN_char_int(lenRpn, vals,varValArrOffset);
                    }
                }
                if(lisLlenRpn!=null){
                    if(lisLlenRpn.length==1){
                        listLen=vals[(char)lisLlenRpn[0]-varValArrOffset];
                    }else {
                        listLen = RpnUtil.calcRPN_char_int(lisLlenRpn, vals,varValArrOffset);
                    }
                }
                processContext.setFieldInfo(fieldInfo);
                processContext.setLen(len);
                processContext.setListLen(listLen);
                fieldProcessors[processorIndex].deProcess(data,res,processContext);
                if(fieldInfo.isVar()){
                    vals[fieldInfo.getPacketField_var()-varValArrOffset]=((Number)data).intValue();
                }
            }
        } catch (IllegalAccessException e) {
            throw BaseRuntimeException.getException(e);
        }
    }

    public final <T>String toHex(T t){
        ByteBuf byteBuf= toByteBuf(t);
        if(byteBuf==null){
            return "";
        }else{
            return ByteBufUtil.hexDump(byteBuf);
        }
    }

    public final ByteBuf toByteBuf(Object t){
        if(t==null){
            return null;
        }else {
            ByteBuf res = Unpooled.buffer();
            deParse(t, res);
            return res;
        }
    }

    public boolean isEnableOffsetField() {
        return enableOffsetField;
    }

    public void setEnableOffsetField(boolean enableOffsetField) {
        this.enableOffsetField = enableOffsetField;
    }

    public ByteProcessor getByteProcessor() {
        return byteProcessor;
    }

    public void setByteProcessor(ByteProcessor byteProcessor) {
        this.byteProcessor = byteProcessor;
    }

    public ShortProcessor getShortProcessor() {
        return shortProcessor;
    }

    public void setShortProcessor(ShortProcessor shortProcessor) {
        this.shortProcessor = shortProcessor;
    }

    public IntegerProcessor getIntegerProcessor() {
        return integerProcessor;
    }

    public void setIntegerProcessor(IntegerProcessor integerProcessor) {
        this.integerProcessor = integerProcessor;
    }

    public LongProcessor getLongProcessor() {
        return longProcessor;
    }

    public void setLongProcessor(LongProcessor longProcessor) {
        this.longProcessor = longProcessor;
    }

    public ByteArrayProcessor getByteArrayProcessor() {
        return byteArrayProcessor;
    }

    public void setByteArrayProcessor(ByteArrayProcessor byteArrayProcessor) {
        this.byteArrayProcessor = byteArrayProcessor;
    }

    public ShortArrayProcessor getShortArrayProcessor() {
        return shortArrayProcessor;
    }

    public void setShortArrayProcessor(ShortArrayProcessor shortArrayProcessor) {
        this.shortArrayProcessor = shortArrayProcessor;
    }

    public IntegerArrayProcessor getIntegerArrayProcessor() {
        return integerArrayProcessor;
    }

    public void setIntegerArrayProcessor(IntegerArrayProcessor integerArrayProcessor) {
        this.integerArrayProcessor = integerArrayProcessor;
    }

    public LongArrayProcessor getLongArrayProcessor() {
        return longArrayProcessor;
    }

    public void setLongArrayProcessor(LongArrayProcessor longArrayProcessor) {
        this.longArrayProcessor = longArrayProcessor;
    }

    public StringProcessor getStringProcessor() {
        return stringProcessor;
    }

    public void setStringProcessor(StringProcessor stringProcessor) {
        this.stringProcessor = stringProcessor;
    }

    public DateProcessor getDateProcessor() {
        return dateProcessor;
    }

    public void setDateProcessor(DateProcessor dateProcessor) {
        this.dateProcessor = dateProcessor;
    }

    public ByteBufProcessor getByteBufProcessor() {
        return byteBufProcessor;
    }

    public void setByteBufProcessor(ByteBufProcessor byteBufProcessor) {
        this.byteBufProcessor = byteBufProcessor;
    }

    public ListProcessor getListProcessor() {
        return listProcessor;
    }

    public void setListProcessor(ListProcessor listProcessor) {
        this.listProcessor = listProcessor;
    }
}
