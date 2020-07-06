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
    protected FieldProcessor byteProcessor=new ByteProcessor();
    protected FieldProcessor shortProcessor=new ShortProcessor();
    protected FieldProcessor integerProcessor=new IntegerProcessor();
    protected FieldProcessor longProcessor=new LongProcessor();
    protected FieldProcessor<byte[]> byteArrayProcessor=new ByteArrayProcessor();
    protected FieldProcessor<short[]> shortArrayProcessor=new ShortArrayProcessor();
    protected FieldProcessor<int[]> integerArrayProcessor=new IntegerArrayProcessor();
    protected FieldProcessor<long[]> longArrayProcessor=new LongArrayProcessor();
    protected FieldProcessor<String> stringProcessor=new StringProcessor();
    protected FieldProcessor<Date> dateProcessor=new DateProcessor();
    protected FieldProcessor<ByteBuf> byteBufProcessor=new ByteBufProcessor();
    protected FieldProcessor<List> listProcessor=new ListProcessor();

    public void init(){
        //初始化处理器
        initProcessor();
        //初始化解析实体对象
        initPacketInfo();
        //设置处理器
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
            for (FieldInfo fieldInfo : packetInfo.getFieldInfos()) {
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
                OffsetFieldInfo[] offsetFieldInfos = packetInfo.getOffsetFieldInfos();
                if (offsetFieldInfos != null && offsetFieldInfos.length>0) {
                    //define temp var
                    Object sourceVal;
                    double destVal;
                    int fieldType;
                    for (OffsetFieldInfo offsetFieldInfo : offsetFieldInfos) {
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
            for (FieldInfo fieldInfo : packetInfo.getFieldInfos()) {
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

    public Map<Class, PacketInfo> getPacketInfoCache() {
        return packetInfoCache;
    }

    public FieldProcessor[] getFieldProcessors() {
        return fieldProcessors;
    }

    public void setFieldProcessors(FieldProcessor[] fieldProcessors) {
        this.fieldProcessors = fieldProcessors;
    }

    public List<FieldProcessor> getBaseProcessorList() {
        return baseProcessorList;
    }

    public void setBaseProcessorList(List<FieldProcessor> baseProcessorList) {
        this.baseProcessorList = baseProcessorList;
    }

    public FieldProcessor getByteProcessor() {
        return byteProcessor;
    }

    public void setByteProcessor(FieldProcessor byteProcessor) {
        this.byteProcessor = byteProcessor;
    }

    public FieldProcessor getShortProcessor() {
        return shortProcessor;
    }

    public void setShortProcessor(FieldProcessor shortProcessor) {
        this.shortProcessor = shortProcessor;
    }

    public FieldProcessor getIntegerProcessor() {
        return integerProcessor;
    }

    public void setIntegerProcessor(FieldProcessor integerProcessor) {
        this.integerProcessor = integerProcessor;
    }

    public FieldProcessor getLongProcessor() {
        return longProcessor;
    }

    public void setLongProcessor(FieldProcessor longProcessor) {
        this.longProcessor = longProcessor;
    }

    public FieldProcessor<byte[]> getByteArrayProcessor() {
        return byteArrayProcessor;
    }

    public void setByteArrayProcessor(FieldProcessor<byte[]> byteArrayProcessor) {
        this.byteArrayProcessor = byteArrayProcessor;
    }

    public FieldProcessor<short[]> getShortArrayProcessor() {
        return shortArrayProcessor;
    }

    public void setShortArrayProcessor(FieldProcessor<short[]> shortArrayProcessor) {
        this.shortArrayProcessor = shortArrayProcessor;
    }

    public FieldProcessor<int[]> getIntegerArrayProcessor() {
        return integerArrayProcessor;
    }

    public void setIntegerArrayProcessor(FieldProcessor<int[]> integerArrayProcessor) {
        this.integerArrayProcessor = integerArrayProcessor;
    }

    public FieldProcessor<long[]> getLongArrayProcessor() {
        return longArrayProcessor;
    }

    public void setLongArrayProcessor(FieldProcessor<long[]> longArrayProcessor) {
        this.longArrayProcessor = longArrayProcessor;
    }

    public FieldProcessor<String> getStringProcessor() {
        return stringProcessor;
    }

    public void setStringProcessor(FieldProcessor<String> stringProcessor) {
        this.stringProcessor = stringProcessor;
    }

    public FieldProcessor<Date> getDateProcessor() {
        return dateProcessor;
    }

    public void setDateProcessor(FieldProcessor<Date> dateProcessor) {
        this.dateProcessor = dateProcessor;
    }

    public FieldProcessor<ByteBuf> getByteBufProcessor() {
        return byteBufProcessor;
    }

    public void setByteBufProcessor(FieldProcessor<ByteBuf> byteBufProcessor) {
        this.byteBufProcessor = byteBufProcessor;
    }

    public FieldProcessor<List> getListProcessor() {
        return listProcessor;
    }

    public void setListProcessor(FieldProcessor<List> listProcessor) {
        this.listProcessor = listProcessor;
    }
}
