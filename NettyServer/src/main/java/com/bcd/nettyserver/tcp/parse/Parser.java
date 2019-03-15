package com.bcd.nettyserver.tcp.parse;

import com.bcd.base.exception.BaseRuntimeException;
import com.bcd.base.util.SpringUtil;
import com.bcd.nettyserver.tcp.anno.PacketField;
import com.bcd.nettyserver.tcp.info.PacketInfo;
import com.bcd.nettyserver.tcp.parse.impl.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Parser{
    private final static ScriptEngineManager MANAGER = new ScriptEngineManager();
    public final static Map<String,PacketInfo> PACKET_INFO_CACHE=new ConcurrentHashMap<>();

    private FieldParser<Byte> byteFieldParser;
    private FieldParser<byte[]> byteArrayFieldParser;
    private FieldParser<Short> shortFieldParser;
    private FieldParser<short[]> shortArrayFieldParser;
    private FieldParser<Integer> integerFieldParser;
    private FieldParser<int[]> integerArrayFieldParser;
    private FieldParser<Long> longFieldParser;
    private FieldParser<long[]> longArrayFieldParser;
    private FieldParser<String> stringFieldParser;
    private FieldParser<Date> dateFieldParser;

    public Parser() {
        this.byteFieldParser = ByteFieldParser.INSTANCE;
        this.byteArrayFieldParser = ByteArrayFieldParser.INSTANCE;
        this.shortFieldParser = ShortFieldParser.INSTANCE;
        this.shortArrayFieldParser = ShortArrayFieldParser.INSTANCE;
        this.integerFieldParser = IntegerFieldParser.INSTANCE;
        this.integerArrayFieldParser = IntegerArrayFieldParser.INSTANCE;
        this.longFieldParser = LongFieldParser.INSTANCE;
        this.longArrayFieldParser = LongArrayFieldParser.INSTANCE;
        this.stringFieldParser = StringFieldParser.INSTANCE;
        this.dateFieldParser = DateFieldParser.INSTANCE;
    }

    public FieldParser<Byte> getByteFieldParser() {
        return byteFieldParser;
    }

    public FieldParser<byte[]> getByteArrayFieldParser() {
        return byteArrayFieldParser;
    }

    public FieldParser<Short> getShortFieldParser() {
        return shortFieldParser;
    }

    public FieldParser<short[]> getShortArrayFieldParser() {
        return shortArrayFieldParser;
    }

    public FieldParser<Integer> getIntegerFieldParser() {
        return integerFieldParser;
    }

    public FieldParser<int[]> getIntegerArrayFieldParser() {
        return integerArrayFieldParser;
    }

    public FieldParser<Long> getLongFieldParser() {
        return longFieldParser;
    }

    public FieldParser<long[]> getLongArrayFieldParser() {
        return longArrayFieldParser;
    }

    public FieldParser<String> getStringFieldParser() {
        return stringFieldParser;
    }

    public FieldParser<Date> getDateFieldParser() {
        return dateFieldParser;
    }

    /**
     * 根据类型和缓冲数据生成对应对象
     * 所有涉及解析对象必须有空参数的构造方法
     * @param clazz
     * @param data
     * @param <T>
     * @return
     */
    public <T>T parse(Class<T> clazz,ByteBuf data){
        //1、获取包信息
        PacketInfo packetInfo=toPacketInfo(clazz);
        try {
            //2、构造实例
            T instance= clazz.newInstance();
            //3、进行解析
            List<Field> fieldList1=packetInfo.getFieldList1();
            List<PacketField> annoList1=packetInfo.getAnnoList1();
            ScriptEngine engine= MANAGER.getEngineByName("js");
            for(int i=0;i<=annoList1.size()-1;i++){
                PacketField packetField= annoList1.get(i);
                Field field=fieldList1.get(i);
                Class fieldType= field.getType();
                Object val;
                Class handleClass=packetField.handleClass();
                if(handleClass==Void.class) {
                    //3.1、如果处理类为空
                    String listLenExpr = packetField.listLenExpr();
                    if (listLenExpr.isEmpty()) {
                        int len;
                        //3.1.1、如果字段不为对象集合,则取出其长度,按照固定长度方式解析
                        if (packetField.lenExpr().isEmpty()) {
                            len = packetField.len();
                        } else {
                            String expr = packetField.lenExpr();
                            len = ((Number) engine.eval(expr)).intValue();
                        }
                        byte[] tempData = new byte[len];
                        data.readBytes(tempData);

                        if (Byte.class.isAssignableFrom(fieldType) || Byte.TYPE.isAssignableFrom(fieldType)) {
                            val = byteFieldParser.parse(tempData);
                        } else if (Short.class.isAssignableFrom(fieldType) || Short.TYPE.isAssignableFrom(fieldType)) {
                            val = shortFieldParser.parse(tempData);
                        } else if (Integer.class.isAssignableFrom(fieldType) || Integer.TYPE.isAssignableFrom(fieldType)) {
                            val = integerFieldParser.parse(tempData);
                        } else if (Long.class.isAssignableFrom(fieldType) || Long.TYPE.isAssignableFrom(fieldType)) {
                            val = longFieldParser.parse(tempData);
                        } else if (String.class.isAssignableFrom(fieldType)) {
                            val = stringFieldParser.parse(tempData);
                        } else if (Date.class.isAssignableFrom(fieldType)) {
                            val = dateFieldParser.parse(tempData);
                        } else if (fieldType.isArray()) {
                            Class arrType = fieldType.getComponentType();
                            if (Byte.class.isAssignableFrom(arrType) || Byte.TYPE.isAssignableFrom(arrType)) {
                                val = byteArrayFieldParser.parse(tempData, packetField.singleLen());
                            } else if (Short.class.isAssignableFrom(arrType) || Short.TYPE.isAssignableFrom(arrType)) {
                                val = shortArrayFieldParser.parse(tempData, packetField.singleLen());
                            } else if (Integer.class.isAssignableFrom(arrType) || Integer.TYPE.isAssignableFrom(arrType)) {
                                val = integerArrayFieldParser.parse(tempData, packetField.singleLen());
                            } else if (Long.class.isAssignableFrom(arrType) || Long.TYPE.isAssignableFrom(arrType)) {
                                val = longArrayFieldParser.parse(tempData, packetField.singleLen());
                            } else {
                                throw BaseRuntimeException.getException("Class[" + instance.getClass().getName() + "] Field[" + field.getName() + "] Array Type[" + arrType.getName() + "] Not Support");
                            }
                        } else if (List.class.isAssignableFrom(fieldType)) {
                            Class listType = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                            if (Byte.class.isAssignableFrom(listType) || Byte.TYPE.isAssignableFrom(listType)) {
                                val = Arrays.asList(byteArrayFieldParser.parse(tempData, packetField.singleLen()));
                            } else if (Short.class.isAssignableFrom(listType) || Short.TYPE.isAssignableFrom(listType)) {
                                val = Arrays.asList(shortArrayFieldParser.parse(tempData, packetField.singleLen()));
                            } else if (Integer.class.isAssignableFrom(listType) || Integer.TYPE.isAssignableFrom(listType)) {
                                val = Arrays.asList(integerArrayFieldParser.parse(tempData, packetField.singleLen()));
                            } else if (Long.class.isAssignableFrom(listType) || Long.TYPE.isAssignableFrom(listType)) {
                                val = Arrays.asList(longArrayFieldParser.parse(tempData, packetField.singleLen()));
                            } else {
                                throw BaseRuntimeException.getException("Class[" + instance.getClass().getName() + "] Field[" + field.getName() + "] List Type[" + listType.getName() + "] Not Support");
                            }
                        } else {
                            //3.1.1.1、如果为实体类型,则解析实体类
                            val = parse(fieldType, data);
                        }
                    } else {
                        //3.1.2、如果字段为对象集合,则特殊处理,按照List泛型内部对象结构和长度依次循环读取字节解析
                        if (List.class.isAssignableFrom(fieldType)) {
                            Class listType = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                            int listLen = ((Number) engine.eval(listLenExpr)).intValue();
                            List list = new ArrayList(listLen);
                            for (int j = 1; j <= listLen; j++) {
                                list.add(parse(listType, data));
                            }
                            val = list;
                        } else {
                            throw BaseRuntimeException.getException("Class[" + clazz.getName() + "] Field[" + field.getName() + "] Must Be List Type");
                        }
                    }
                }else{
                    //3.2、如果处理类不为空
                    FieldHandler<T> fieldHandler=(FieldHandler) SpringUtil.applicationContext.getBean(handleClass);
                    if(fieldHandler==null){
                        fieldHandler=(FieldHandler)handleClass.newInstance();
                    }
                    val=fieldHandler.handle(data,(T)instance);
                }
                if(!packetField.var().isEmpty()){
                    engine.put(packetField.var(),val);
                }
                field.setAccessible(true);
                field.set(instance,val);
            }
            return instance;
        } catch (InstantiationException |IllegalAccessException |ScriptException e) {
            throw BaseRuntimeException.getException(e);
        }
    }


    /**
     * 解析类转换成包信息
     * @param clazz
     * @return
     */
    public PacketInfo toPacketInfo(Class clazz){
        return PACKET_INFO_CACHE.computeIfAbsent(clazz.getName(),(className)->{
            List<Field> allFieldList= FieldUtils.getAllFieldsList(clazz);
            //1、处理@PacketField字段
            //1.1、过滤出@PacketField注解字段并按照index排序
            List<Field> fieldList1=allFieldList.stream().filter(field -> field.getAnnotation(PacketField.class)!=null).sorted((f1,f2)->{
                int i1=f1.getAnnotation(PacketField.class).index();
                int i2=f2.getAnnotation(PacketField.class).index();
                if(i1<i2){
                    return -1;
                }else if(i1>i2){
                    return 1;
                }else{
                    return 0;
                }
            }).collect(Collectors.toList());
            //2、转换成注解
            List<PacketField> annoList1= fieldList1.stream().map(field -> field.getAnnotation(PacketField.class)).collect(Collectors.toList());

            //3、找出头字段注解和长度字段注解
            PacketField headPacketField=null;
            PacketField contentLengthPacketField=null;
            for (Field field : fieldList1) {
                PacketField curField=field.getAnnotation(PacketField.class);
                if(!curField.headValue().isEmpty()){
                    headPacketField=curField;
                }else if(curField.isLengthField()){
                    contentLengthPacketField=curField;
                }
                if(headPacketField!=null&&contentLengthPacketField!=null){
                    break;
                }
            }
            //4、设置信息
            PacketInfo packetInfo=new PacketInfo();
            //4.1、头解析
            if(headPacketField!=null){
                byte[] header=null;
                String val=headPacketField.headValue();
                Integer headerVal=Integer.parseInt(val,16);
                ByteBuf byteBuf= Unpooled.buffer();
                if(headerVal<Byte.MAX_VALUE&&headerVal>=Byte.MIN_VALUE){
                    byteBuf.writeByte(headerVal);
                }else if(headerVal<Short.MAX_VALUE&&headerVal>=Short.MIN_VALUE){
                    byteBuf.writeShort(headerVal);
                }else if(headerVal<Integer.MAX_VALUE&&headerVal>=Integer.MIN_VALUE){
                    byteBuf.writeInt(headerVal);
                }
                while(byteBuf.isReadable()){
                    header= ArrayUtils.add(header,byteBuf.readByte());
                }
                packetInfo.setHeader(header);
            }
            //4.2、长度解析
            if(contentLengthPacketField!=null) {
                int len=0;
                for (PacketField curField : annoList1) {
                    if (curField.index() < contentLengthPacketField.index()) {
                        len += curField.len();
                    } else {
                        break;
                    }
                }
                packetInfo.setLengthFieldStart(len);
                packetInfo.setLengthFieldEnd(packetInfo.getLengthFieldStart()+contentLengthPacketField.len());
            }
            //4.3、填充字段信息和注解信息
            packetInfo.setFieldList1(fieldList1);
            packetInfo.setAnnoList1(annoList1);
            return packetInfo;
        });
    }

    public Parser withByteFieldParser(FieldParser<Byte> parser){
        this.byteFieldParser=parser;
        return this;
    }

    public Parser withByteArrayFieldParser(FieldParser<byte[]> parser){
        this.byteArrayFieldParser=parser;
        return this;
    }

    public Parser withDateFieldParser(FieldParser<Date> parser){
        this.dateFieldParser=parser;
        return this;
    }

    public Parser withIntegerFieldParser(FieldParser<Integer> parser){
        this.integerFieldParser=parser;
        return this;
    }

    public Parser withIntegerArrayFieldParser(FieldParser<int[]> parser){
        this.integerArrayFieldParser =parser;
        return this;
    }

    public Parser withLongFieldParser(FieldParser<Long> parser){
        this.longFieldParser=parser;
        return this;
    }

    public Parser withLongArrayFieldParser(FieldParser<long[]> parser){
        this.longArrayFieldParser=parser;
        return this;
    }

    public Parser withShortFieldParser(FieldParser<Short> parser){
        this.shortFieldParser=parser;
        return this;
    }

    public Parser withShortArrayFieldParser(FieldParser<short[]> parser){
        this.shortArrayFieldParser=parser;
        return this;
    }

    public Parser withStringFieldParser(FieldParser<String> parser){
        this.stringFieldParser=parser;
        return this;
    }
}
