package com.bcd.nettyserver.tcp.parse;

import com.bcd.base.exception.BaseRuntimeException;
import com.bcd.base.util.ClassUtil;
import com.bcd.base.util.ProxyUtil;
import com.bcd.base.util.SpringUtil;
import com.bcd.base.util.StringUtil;
import com.bcd.nettyserver.tcp.anno.PacketField;
import com.bcd.nettyserver.tcp.info.FieldInfo;
import com.bcd.nettyserver.tcp.info.PacketInfo;
import com.bcd.nettyserver.tcp.parse.impl.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 解析器类,线程安全
 */
@SuppressWarnings("unchecked")
public abstract class Parser{
    public final static Map<String,PacketInfo> PACKET_INFO_CACHE=new ConcurrentHashMap<>();


    protected FieldParser<Byte> byteFieldParser;
    protected FieldParser<byte[]> byteArrayFieldParser;
    protected FieldParser<Short> shortFieldParser;
    protected FieldParser<short[]> shortArrayFieldParser;
    protected FieldParser<Integer> integerFieldParser;
    protected FieldParser<int[]> integerArrayFieldParser;
    protected FieldParser<Long> longFieldParser;
    protected FieldParser<long[]> longArrayFieldParser;
    protected FieldParser<String> stringFieldParser;
    protected FieldParser<Date> dateFieldParser;
    protected FieldParser<ByteBuf> byteBufFieldParser;
    /**
     * 用于处理时候直接通过索引拿到对应解析器
     * 索引对应 {@link FieldInfo#type}
     */
    protected FieldParser[] fieldParserArr=new FieldParser[20];

    /**
     * {@link PacketField#handleClass()} 对应的处理类
     */
    protected Map<Class,FieldHandler> classToHandler =new HashMap<>();

    public Parser() {
    }

    public void init(){
        //初始化解析器
        initParser();
        //初始化处理器
        initHandler();
        afterInit();
    }

    protected void initParser(){
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
        this.byteBufFieldParser=ByteBufFieldParser.INSTANCE;

        fieldParserArr[1]=byteFieldParser;
        fieldParserArr[2]=shortFieldParser;
        fieldParserArr[3]=integerFieldParser;
        fieldParserArr[4]=longFieldParser;
        fieldParserArr[5]=stringFieldParser;
        fieldParserArr[6]=dateFieldParser;
        fieldParserArr[7]=byteArrayFieldParser;
        fieldParserArr[8]=shortArrayFieldParser;
        fieldParserArr[9]=integerArrayFieldParser;
        fieldParserArr[10]=longArrayFieldParser;
        fieldParserArr[11]=byteArrayFieldParser;
        fieldParserArr[12]=shortArrayFieldParser;
        fieldParserArr[13]=integerArrayFieldParser;
        fieldParserArr[14]=longArrayFieldParser;
        fieldParserArr[15]=byteBufFieldParser;
    }

    protected abstract void initHandler();

    protected void afterInit(){
        //为handler设置parser
        classToHandler.values().forEach(e->{
            e.setParser(this);
        });
    }

    /**
     * 初始化 classToHandler
     * 通过spring
     */
    protected void initHandlerBySpring(){
        SpringUtil.applicationContext.getBeansOfType(FieldHandler.class).values().forEach(e->{
            Class clazz= ProxyUtil.getSource(e).getClass();
            classToHandler.put(clazz,e);
        });
    }

    /**
     * 初始化 classToHandler
     * 去除接口和抽象类
     * 通过扫描
     */
    protected void initHandlerByScanClass(String packageName){
        try {
            for (Class e : ClassUtil.getClassesByParentClass(FieldHandler.class, packageName)) {
                classToHandler.put(e,(FieldHandler) e.newInstance());
            }
        } catch (IOException | ClassNotFoundException |IllegalAccessException |InstantiationException e) {
            throw BaseRuntimeException.getException(e);
        }
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

    public <T>String toHex(T t){
        try{
            if(t==null){
                return "";
            }else{
                StringBuilder sb=new StringBuilder();
                Class clazz= t.getClass();
                //解析包
                Map<String,Double> valMap=new HashMap<>();
                PacketInfo packetInfo=toPacketInfo(clazz);
                List<FieldInfo> fieldInfoList=packetInfo.getFieldInfoList();
                for(int i=0;i<=fieldInfoList.size()-1;i++){
                    StringBuilder val=new StringBuilder();
                    FieldInfo fieldInfo= fieldInfoList.get(i);
                    Object data=fieldInfo.getField().get(t);
                    int type=fieldInfo.getType();
                    PacketField packetField= fieldInfo.getPacketField();
                    /**
                     * rpns[0] 代表 {@link PacketField#lenExpr()}
                     * rpns[1] 代表 {@link PacketField#listLenExpr()}
                     */
                    List<String>[] rpns= fieldInfo.getRpns();
                    if(type==0){
                        /**
                         * {@link PacketField#handleClass()} 不为空
                         * 特殊处理
                         */
                        Class handleClass=fieldInfo.getClazz();
                        FieldHandler fieldHandler= classToHandler.get(handleClass);
                        if(fieldHandler==null){
                            throw BaseRuntimeException.getException("cant't find class["+handleClass.getName()+"] handler");
                        }
                        val.append(fieldHandler.toHex(data));
                    }else{
                        if(type==101){
                            /**
                             * 如果{@link PacketField#listLenExpr()} ()} 不为空
                             * 处理List<Bean>类型字段
                             */
                            int listLen = StringUtil.calcRPN(rpns[1],valMap).intValue();
                            List list = (List)data;
                            for (int j = 0; j <= listLen-1; j++) {
                                val.append(toHex(list.get(j)));
                            }
                        }else{
                            if(type==100){
                                /**
                                 * 处理Bean 类型数据
                                 */
                                val.append(toHex(data));
                            }else{
                                int len;
                                /**
                                 * 如果{@link PacketField#lenExpr()} 为空
                                 */
                                if(rpns[0]==null){
                                    len=packetField.len();
                                }else{
                                    len=StringUtil.calcRPN(rpns[0],valMap).intValue();
                                }
                                val.append(fieldParserArr[type].toHex(data,len,packetField.singleLen()));
                                /**
                                 * 如果 {@link PacketField#var()} 不为空
                                 * 说明是变量
                                 */
                                if(fieldInfo.isVar()){
                                    valMap.put(packetField.var(),((Number)data).doubleValue());
                                }

                            }
                        }
                    }
                    sb.append(val);
                }
                return sb.toString();
            }
        } catch (IllegalAccessException e) {
            throw BaseRuntimeException.getException(e);
        }
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
        //解析包
        PacketInfo packetInfo=toPacketInfo(clazz);
        try {
            //构造实例
            T instance= clazz.newInstance();
            //进行解析
            List<FieldInfo> fieldInfoList=packetInfo.getFieldInfoList();
            Map<String,Double> valMap=new HashMap<>();
            for(int i=0;i<=fieldInfoList.size()-1;i++){
                FieldInfo fieldInfo= fieldInfoList.get(i);
                Object val;
                int type=fieldInfo.getType();
                PacketField packetField= fieldInfo.getPacketField();
                /**
                 * rpns[0] 代表 {@link PacketField#lenExpr()}
                 * rpns[1] 代表 {@link PacketField#listLenExpr()}
                 */
                List<String>[] rpns= fieldInfo.getRpns();
                if(type==0){
                    /**
                     * {@link PacketField#handleClass()} 不为空
                     * 特殊处理
                     */
                    Class handleClass=fieldInfo.getClazz();
                    FieldHandler fieldHandler= classToHandler.get(handleClass);
                    if(fieldHandler==null){
                        throw BaseRuntimeException.getException("cant't find class["+handleClass.getName()+"] handler");
                    }
                    val=fieldHandler.handle(data,instance);
                }else{
                    if(type==101){
                        /**
                         * 如果{@link PacketField#listLenExpr()} ()} 不为空
                         * 处理List<Bean>类型字段
                         */
                        int listLen = StringUtil.calcRPN(rpns[1],valMap).intValue();
                        List list = new ArrayList(listLen);
                        for (int j = 1; j <= listLen; j++) {
                            list.add(parse(fieldInfo.getClazz(), data));
                        }
                        val = list;
                    }else{
                        if(type==100){
                            /**
                             * 处理Bean 类型数据
                             */
                            val = parse(fieldInfo.getClazz(), data);
                        }else{
                            int len;
                            /**
                             * 如果{@link PacketField#lenExpr()} 为空
                             */
                            if(rpns[0]==null){
                                len=packetField.len();
                            }else{
                                len=StringUtil.calcRPN(rpns[0],valMap).intValue();
                            }
                            val=fieldParserArr[type].parse(data,len,packetField.singleLen());

                            /**
                             * 如果 {@link PacketField#var()} 不为空
                             * 说明是变量
                             */
                            if(fieldInfo.isVar()){
                                valMap.put(packetField.var(),((Number)val).doubleValue());
                            }

                        }
                    }
                }
                fieldInfo.getField().set(instance,val);
            }
            return instance;
        } catch (InstantiationException |IllegalAccessException e) {
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
            /**
             * 1、过滤所有带{@link PacketField}的字段
             * 2、将字段按照{@link PacketField#index()}正序
             * 3、将每个字段类型解析成FieldInfo
             */
            List<FieldInfo> fieldInfoList=allFieldList.stream().filter(field -> field.getAnnotation(PacketField.class)!=null).sorted((f1, f2)->{
                int i1=f1.getAnnotation(PacketField.class).index();
                int i2=f2.getAnnotation(PacketField.class).index();
                if(i1<i2){
                    return -1;
                }else if(i1>i2){
                    return 1;
                }else{
                    return 0;
                }
            }).map(field->{
                field.setAccessible(true);
                PacketField packetField= field.getAnnotation(PacketField.class);
                Class fieldType=field.getType();
                int type;
                Class typeClazz=null;
                boolean isVar=false;
                //判断是否特殊处理
                if(packetField.handleClass()==Void.class){
                    //判断是否是List<Bean>(Bean代表自定义实体类型,不包括Byte、Short、Integer、Long)
                    if(packetField.listLenExpr().isEmpty()) {
                        if (Byte.class.isAssignableFrom(fieldType) || Byte.TYPE.isAssignableFrom(fieldType)) {
                            type = 1;
                        } else if (Short.class.isAssignableFrom(fieldType) || Short.TYPE.isAssignableFrom(fieldType)) {
                            type = 2;
                        } else if (Integer.class.isAssignableFrom(fieldType) || Integer.TYPE.isAssignableFrom(fieldType)) {
                            type = 3;
                        } else if (Long.class.isAssignableFrom(fieldType) || Long.TYPE.isAssignableFrom(fieldType)) {
                            type = 4;
                        } else if (String.class.isAssignableFrom(fieldType)) {
                            type = 5;
                        } else if (Date.class.isAssignableFrom(fieldType)) {
                            type = 6;
                        } else if (fieldType.isArray()) {
                            //数组类型
                            Class arrType = fieldType.getComponentType();
                            if (Byte.class.isAssignableFrom(arrType) || Byte.TYPE.isAssignableFrom(arrType)) {
                                type = 7;
                            } else if (Short.class.isAssignableFrom(arrType) || Short.TYPE.isAssignableFrom(arrType)) {
                                type = 8;
                            } else if (Integer.class.isAssignableFrom(arrType) || Integer.TYPE.isAssignableFrom(arrType)) {
                                type = 9;
                            } else if (Long.class.isAssignableFrom(arrType) || Long.TYPE.isAssignableFrom(arrType)) {
                                type = 10;
                            } else {
                                throw BaseRuntimeException.getException("Class[" + clazz.getName() + "] Field[" + field.getName() + "] Array Type[" + arrType.getName() + "] Not Support");
                            }
                        } else if (List.class.isAssignableFrom(fieldType)) {
                            //简单类型的集合
                            Class listType = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                            if (Byte.class.isAssignableFrom(listType) || Byte.TYPE.isAssignableFrom(listType)) {
                                type = 11;
                            } else if (Short.class.isAssignableFrom(listType) || Short.TYPE.isAssignableFrom(listType)) {
                                type = 12;
                            } else if (Integer.class.isAssignableFrom(listType) || Integer.TYPE.isAssignableFrom(listType)) {
                                type = 13;
                            } else if (Long.class.isAssignableFrom(listType) || Long.TYPE.isAssignableFrom(listType)) {
                                type = 14;
                            } else {
                                throw BaseRuntimeException.getException("Class[" + clazz.getClass().getName() + "] Field[" + field.getName() + "] List Type[" + listType.getName() + "] Not Support");
                            }
                        } else if(ByteBuf.class.isAssignableFrom(fieldType)){
                            //ByteBuf类型
                            type=15;
                        } else {
                            //实体类型
                            type = 100;
                            typeClazz = fieldType;
                        }
                    }else{
                        //实体类型集合
                        type=101;
                        typeClazz = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    }
                }else{
                    //特殊处理
                    type=0;
                    typeClazz=packetField.handleClass();
                }

                //转换逆波兰表达式
                List<String>[] rpns=new List[2];
                if(!packetField.lenExpr().isEmpty()){
                    rpns[0]= StringUtil.parseArithmeticToRPN(packetField.lenExpr());
                }
                if(!packetField.listLenExpr().isEmpty()){
                    rpns[1]= StringUtil.parseArithmeticToRPN(packetField.listLenExpr());
                }

                //判断是否是变量
                if(!packetField.var().isEmpty()){
                    isVar=true;
                }

                FieldInfo fieldInfo=new FieldInfo();
                fieldInfo.setField(field);
                fieldInfo.setType(type);
                fieldInfo.setVar(isVar);
                fieldInfo.setClazz(typeClazz);
                fieldInfo.setPacketField(packetField);
                fieldInfo.setRpns(rpns);
                return fieldInfo;
            }).collect(Collectors.toList());

            PacketInfo packetInfo=new PacketInfo();
            //找出头字段注解和长度字段注解
            PacketField headPacketField=null;
            PacketField contentLengthPacketField=null;
            for (FieldInfo fieldInfo : fieldInfoList) {
                PacketField curField=fieldInfo.getPacketField();
                if(!curField.headValue().isEmpty()){
                    headPacketField=curField;
                }else if(curField.isLengthField()){
                    contentLengthPacketField=curField;
                }
                if(headPacketField!=null&&contentLengthPacketField!=null){
                    break;
                }
            }
            //头解析
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
            //长度解析
            if(contentLengthPacketField!=null) {
                int len=0;
                for (FieldInfo fieldInfo : fieldInfoList) {
                    PacketField curField=fieldInfo.getPacketField();
                    if (curField.index() < contentLengthPacketField.index()) {
                        len += curField.len();
                    } else {
                        break;
                    }
                }
                packetInfo.setLengthFieldStart(len);
                packetInfo.setLengthFieldEnd(packetInfo.getLengthFieldStart()+contentLengthPacketField.len());
            }
            packetInfo.setFieldInfoList(fieldInfoList);
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
