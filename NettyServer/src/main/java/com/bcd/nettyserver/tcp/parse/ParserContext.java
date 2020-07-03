package com.bcd.nettyserver.tcp.parse;

import com.bcd.base.exception.BaseRuntimeException;
import com.bcd.base.util.*;
import com.bcd.nettyserver.tcp.anno.OffsetField;
import com.bcd.nettyserver.tcp.anno.ParseAble;
import com.bcd.nettyserver.tcp.anno.PacketField;
import com.bcd.nettyserver.tcp.info.FieldInfo;
import com.bcd.nettyserver.tcp.info.OffsetFieldInfo;
import com.bcd.nettyserver.tcp.info.PacketInfo;
import com.bcd.nettyserver.tcp.parse.impl.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 解析器类,线程安全
 * 性能分析:
 * 以gb32960协议为测试样本:
 * 20w/s
 *
 */
@SuppressWarnings("unchecked")
public abstract class ParserContext {

    public final Map<Class, PacketInfo> packetInfoCache =new HashMap<>();

    /**
     * 启用偏移字段值处理
     */
    protected boolean enableOffsetField=false;

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
     * {@link PacketField#parserClass()} 对应的处理类
     */
    protected Map<Class, FieldParser> classToParser =new HashMap<>();

    protected String pkg;

    public ParserContext(String pkg) {
        this.pkg=pkg;
    }

    public void init(){
        //初始化解析器
        initParser();
        //初始化处理器
        initHandler();
        /**
         * 加载所有 {@link ParseAble} 注解的类并转换为 {@link PacketInfo}
         */
        initPacketInfo(pkg);

        afterInit();
    }

    protected void initParser(){
        if(this.byteFieldParser==null) {
            this.byteFieldParser = new ByteFieldParser();
        }
        if(this.byteArrayFieldParser==null) {
            this.byteArrayFieldParser = new ByteArrayFieldParser();
        }
        if(this.shortFieldParser==null) {
            this.shortFieldParser = new ShortFieldParser();
        }
        if(this.shortArrayFieldParser==null) {
            this.shortArrayFieldParser = new ShortArrayFieldParser();
        }
        if(this.integerFieldParser==null) {
            this.integerFieldParser = new IntegerFieldParser();
        }
        if(this.integerArrayFieldParser==null) {
            this.integerArrayFieldParser = new IntegerArrayFieldParser();
        }
        if(this.longFieldParser==null) {
            this.longFieldParser = new LongFieldParser();
        }
        if(this.longArrayFieldParser==null) {
            this.longArrayFieldParser = new LongArrayFieldParser();
        }
        if(this.stringFieldParser==null) {
            this.stringFieldParser = new StringFieldParser();
        }
        if(this.dateFieldParser==null) {
            this.dateFieldParser = new DateFieldParser();
        }
        if(this.byteBufFieldParser==null) {
            this.byteBufFieldParser = new ByteBufFieldParser();
        }

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
        fieldParserArr[11]=byteBufFieldParser;
    }

    protected abstract void initHandler();

    protected void afterInit(){
        //为handler设置parser
        classToParser.values().forEach(e->{
            e.setContext(this);
        });
    }

    /**
     * 初始化 classToHandler
     * 通过spring
     */
    protected void initHandlerBySpring(){
        SpringUtil.applicationContext.getBeansOfType(FieldParser.class).values().forEach(e->{
            Class clazz= ProxyUtil.getSource(e).getClass();
            classToParser.put(clazz,e);
        });
    }

    /**
     * 初始化 classToParser
     * 去除接口和抽象类
     * 通过扫描
     */
    protected void initParserByScanClass(){
        try {
            for (Class e : ClassUtil.getClassesByParentClass(FieldParser.class, pkg)) {
                classToParser.put(e,(FieldParser) e.newInstance());
            }
        } catch (IOException | ClassNotFoundException |IllegalAccessException |InstantiationException e) {
            throw BaseRuntimeException.getException(e);
        }
    }

    /**
     * 加载所有 {@link ParseAble} 注解的类并转换为 {@link PacketInfo}
     * 存储在map中
     * @param packageName
     */
    public final void initPacketInfo(String packageName){
        try {
            for (Class e : ClassUtil.getClassesWithAnno(ParseAble.class, packageName)) {
                packetInfoCache.put(e,toPacketInfo(e));
            }
        } catch (IOException | ClassNotFoundException e) {
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
            toByteBuf(t, res);
            return res;
        }
    }

    /**
     * 将对象转换为byteBuf
     * @param t 不能为null
     * @param res
     */
    public final void toByteBuf(Object t,ByteBuf res){
        try{
            if(res==null){
                res=Unpooled.buffer();
            }
            Class clazz= t.getClass();
            PacketInfo packetInfo=packetInfoCache.get(clazz);
            //解析包
            int [] vals=null;
            int varValArrLen=packetInfo.getVarValArrLen();
            int varValArrOffset=packetInfo.getVarValArrOffset();
            if(varValArrLen!=0){
                vals=new int[varValArrLen];
            }
            List<FieldInfo> fieldInfoList=packetInfo.getFieldInfoList();
            FieldToByteBufContext context=new FieldToByteBufContext();
            for (int i=0,end=fieldInfoList.size();i<end;i++) {
                FieldInfo fieldInfo=fieldInfoList.get(i);
                context.setFieldInfo(fieldInfo);
                int type=fieldInfo.getType();
                /**
                 * rpns[0] 代表 {@link PacketField#lenExpr()}
                 * rpns[1] 代表 {@link PacketField#listLenExpr()}
                 */
                List[] rpns= fieldInfo.getRpns();
                Object data;
                data=fieldInfo.getField().get(t);
                switch (type){
                    case 0:{
                        /**
                         * {@link PacketField#parserClass()} 不为空
                         * 特殊处理
                         */
                        Class handleClass=fieldInfo.getClazz();
                        FieldParser fieldParser= classToParser.get(handleClass);
//                            if(fieldParser==null){
//                                throw BaseRuntimeException.getException("cant't find class["+handleClass.getName()+"] handler");
//                            }
                        int len;
                        /**
                         * 如果{@link PacketField#lenExpr()} 为空
                         */
                        if(rpns[0]==null){
                            len=fieldInfo.getPacketField_len();
                        }else{
                            if(rpns[0].size()==1){
                                len= vals[(char)rpns[0].get(0)-varValArrOffset];
                            }else {
                                len = RpnUtil.calcRPN_char_int(rpns[0], vals,varValArrOffset);
                            }
                        }
                        fieldParser.toByteBuf(data,len,context,res);
                        break;
                    }
                    case 100:{
                        /**
                         * 处理Bean 类型数据
                         */
                        toByteBuf(data,res);
                        break;
                    }
                    case 101:{
                        /**
                         * 如果{@link PacketField#listLenExpr()} ()} 不为空
                         * 处理List<Bean>类型字段
                         */
                        int listLen = RpnUtil.calcRPN_char_int(rpns[1],vals,varValArrOffset);
                        List list = (List)data;
                        for (int j = 0; j <= listLen-1; j++) {
                            toByteBuf(list.get(j),res);
                        }
                        break;
                    }
                    default:{
                        int len;
                        /**
                         * 如果{@link PacketField#lenExpr()} 为空
                         */
                        if(rpns[0]==null){
                            len=fieldInfo.getPacketField_len();
                        }else{
                            if(rpns[0].size()==1){
                                len= vals[(char)rpns[0].get(0)-varValArrOffset];
                            }else {
                                len = RpnUtil.calcRPN_char_int(rpns[0], vals,varValArrOffset);
                            }
                        }
                        fieldParserArr[type].toByteBuf(data,len,context,res);
                        /**
                         * 如果 {@link PacketField#var()} 不为空
                         * 说明是变量
                         */
                        if(fieldInfo.isVar()){
                            vals[fieldInfo.getPacketField_var()-varValArrOffset]=((Number)data).intValue();
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw BaseRuntimeException.getException(e);
        }
    }

    public final <T>T parse(Class<T> clazz,ByteBuf data){
        return parse(clazz,data,0);
    }

    /**
     * 根据类型和缓冲数据生成对应对象
     * 所有涉及解析对象必须有空参数的构造方法
     * @param clazz
     * @param data
     * @param allLen 0代表无效,此时对象解析不依赖总长度
     * @param <T>
     * @return
     */
    public final <T>T parse(Class<T> clazz,ByteBuf data,int allLen){
        //解析包
        PacketInfo packetInfo=packetInfoCache.get(clazz);
//        if(packetInfo==null){
//            throw BaseRuntimeException.getException("can not find class["+clazz.getName()+"] packetInfo");
//        }
        try {
            //构造实例
            T instance= clazz.newInstance();
            //进行解析
            int [] vals=null;
            int varValArrLen=packetInfo.getVarValArrLen();
            int varValArrOffset=packetInfo.getVarValArrOffset();
            if(varValArrLen!=0){
                vals=new int[varValArrLen];
            }
            FieldParseContext context=new FieldParseContext();
            context.setPacketInfo(packetInfo);
            context.setInstance(instance);
            context.setAllLen(allLen);
            for (FieldInfo fieldInfo : packetInfo.getFieldInfoList()) {
                context.setFieldInfo(fieldInfo);
                int type=fieldInfo.getType();
                /**
                 * rpns[0] 代表 {@link PacketField#lenExpr()}
                 * rpns[1] 代表 {@link PacketField#listLenExpr()}
                 */
                List[] rpns= fieldInfo.getRpns();
                Object val;
                switch (type){
                    case 0:{
                        /**
                         * {@link PacketField#parserClass()} 不为空
                         * 特殊处理
                         */
                        Class parserClass=fieldInfo.getClazz();
                        FieldParser fieldParser= classToParser.get(parserClass);
//                        if(fieldParser==null){
//                            throw BaseRuntimeException.getException("cant't find class["+parserClass.getName()+"] handler");
//                        }
                        int len;
                        /**
                         * 如果{@link PacketField#lenExpr()} 为空
                         */
                        if(rpns[0]==null){
                            len=fieldInfo.getPacketField_len();
                        }else{
                            if(rpns[0].size()==1){
                                len=vals[(char)rpns[0].get(0)-varValArrOffset];
                            }else {
                                len = RpnUtil.calcRPN_char_int(rpns[0], vals,varValArrOffset);
                            }
                        }
                        val=fieldParser.parse(data,len,context);
                        break;
                    }
                    case 100:{
                        /**
                         * 处理Bean 类型数据
                         */
                        int len;
                        /**
                         * 如果{@link PacketField#lenExpr()} 为空
                         */
                        if(rpns[0]==null){
                            len=fieldInfo.getPacketField_len();
                        }else{
                            if(rpns[0].size()==1){
                                len=vals[(char)rpns[0].get(0)-varValArrOffset];
                            }else{
                                len = RpnUtil.calcRPN_char_int(rpns[0], vals,varValArrOffset);
                            }
                        }
                        val = parse(fieldInfo.getClazz(), data,len);
                        break;
                    }
                    case 101:{
                        /**
                         * 如果{@link PacketField#listLenExpr()} ()} 不为空
                         * 处理List<Bean>类型字段
                         */
                        int listLen ;
                        if(rpns[1]==null){
                            listLen=fieldInfo.getPacketField_len();
                        }else{
                            if(rpns[1].size()==1){
                                listLen=vals[(char)rpns[1].get(0)-varValArrOffset];
                            }else{
                                listLen = RpnUtil.calcRPN_char_int(rpns[1], vals,varValArrOffset);
                            }
                        }
                        List list = new ArrayList(listLen);
                        for (int j = 1; j <= listLen; j++) {
                            list.add(parse(fieldInfo.getClazz(), data));
                        }
                        val = list;
                        break;
                    }
                    default:{
                        int len;
                        /**
                         * 如果{@link PacketField#lenExpr()} 为空
                         */
                        if(rpns[0]==null){
                            len=fieldInfo.getPacketField_len();
                        }else{
                            if(rpns[0].size()==1){
                                len=vals[(char)rpns[0].get(0)-varValArrOffset];
                            }else{
                                len = RpnUtil.calcRPN_char_int(rpns[0], vals,varValArrOffset);
                            }
                        }
                        val=fieldParserArr[type].parse(data,len,context);
                        /**
                         * 如果 {@link PacketField#var()} 不为空
                         * 说明是变量
                         */
                        if(fieldInfo.isVar()){
                            vals[fieldInfo.getPacketField_var()-varValArrOffset]=((Number)val).intValue();
                        }
                    }
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
                        map.put("x", ((Number) sourceVal).doubleValue());
                        destVal = RpnUtil.calcRPN_string_double(offsetFieldInfo.getRpn(), map);
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



    /**
     * 解析类转换成包信息
     * @param clazz
     * @return
     */
    public final PacketInfo toPacketInfo(Class clazz){
        String className=clazz.getName();
        PacketInfo packetInfo=new PacketInfo();
        List<Field> allFieldList= FieldUtils.getAllFieldsList(clazz);
        //求出最小var char int和最大var char int
        int[] maxVarInt=new int[1];
        int[] minVarInt=new int[1];
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
            if(packetField.parserClass()==Void.class){
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
                    } else if(ByteBuf.class.isAssignableFrom(fieldType)){
                        //ByteBuf类型
                        type=11;
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
                typeClazz=packetField.parserClass();
            }

            //转换逆波兰表达式
            List[] rpns=new List[2];
            if(!packetField.lenExpr().isEmpty()){
                rpns[0]= RpnUtil.doWithRpnList_char_int(RpnUtil.parseArithmeticToRPN(packetField.lenExpr()));
            }
            if(!packetField.listLenExpr().isEmpty()){
                rpns[1]= RpnUtil.doWithRpnList_char_int(RpnUtil.parseArithmeticToRPN(packetField.listLenExpr()));
            }

            //判断是否变量
            if(packetField.var()!='0'){
                isVar=true;
            }

            //求maxVarInt、minVarInt
            for (List rpn : rpns) {
                if(rpn!=null) {
                    for (Object o : rpn) {
                        if (o instanceof Character) {
                            if (maxVarInt[0]==0||(char) o > maxVarInt[0]) {
                                maxVarInt[0]=(char) o;
                            }
                            if( minVarInt[0]==0||(char) o < minVarInt[0]){
                                minVarInt[0]=(char) o;
                            }
                        }
                    }
                }
            }

            FieldInfo fieldInfo=new FieldInfo();
            fieldInfo.setField(field);
            fieldInfo.setType(type);
            fieldInfo.setVar(isVar);
            fieldInfo.setClazz(typeClazz);
            fieldInfo.setRpns(rpns);
            fieldInfo.setPacketField_index(packetField.index());
            fieldInfo.setPacketField_len(packetField.len());
            fieldInfo.setPacketField_lenExpr(packetField.lenExpr());
            fieldInfo.setPacketField_listLenExpr(packetField.listLenExpr());
            fieldInfo.setPacketField_singleLen(packetField.singleLen());
            fieldInfo.setPacketField_var(packetField.var());
            fieldInfo.setPacketField_parserClass(packetField.parserClass());
            return fieldInfo;
        }).collect(Collectors.toList());
        packetInfo.setFieldInfoList(fieldInfoList);

        if(maxVarInt[0]!=0){
            packetInfo.setVarValArrLen(maxVarInt[0]-minVarInt[0]+1);
            packetInfo.setVarValArrOffset(minVarInt[0]);
        }

        if(enableOffsetField) {
            //解析offsetField注解字段
            List<OffsetFieldInfo> offsetFieldInfoList = allFieldList.stream().filter(field -> field.getAnnotation(OffsetField.class) != null).map(field -> {
                try {
                    OffsetField offsetField = field.getAnnotation(OffsetField.class);
                    OffsetFieldInfo offsetFieldInfo = new OffsetFieldInfo();
                    offsetFieldInfo.setField(field);
                    offsetFieldInfo.setSourceField(clazz.getDeclaredField(offsetField.sourceField()));
                    offsetFieldInfo.setOffsetField_sourceField(offsetField.sourceField());
                    offsetFieldInfo.setOffsetField_expr(offsetField.expr());
                    offsetFieldInfo.setRpn(RpnUtil.doWithRpnList_string_double(RpnUtil.parseArithmeticToRPN(offsetField.expr())));
                    offsetFieldInfo.getField().setAccessible(true);
                    offsetFieldInfo.getSourceField().setAccessible(true);
                    Class fieldType = field.getType();
                    int type;
                    if (Byte.class.isAssignableFrom(fieldType) || Byte.TYPE.isAssignableFrom(fieldType)) {
                        type = 1;
                    } else if (Short.class.isAssignableFrom(fieldType) || Short.TYPE.isAssignableFrom(fieldType)) {
                        type = 2;
                    } else if (Integer.class.isAssignableFrom(fieldType) || Integer.TYPE.isAssignableFrom(fieldType)) {
                        type = 3;
                    } else if (Long.class.isAssignableFrom(fieldType) || Long.TYPE.isAssignableFrom(fieldType)) {
                        type = 4;
                    } else if (Float.class.isAssignableFrom(fieldType) || Float.TYPE.isAssignableFrom(fieldType)) {
                        type = 4;
                    } else if (Double.class.isAssignableFrom(fieldType) || Double.TYPE.isAssignableFrom(fieldType)) {
                        type = 4;
                    } else {
                        throw BaseRuntimeException.getException("class[" + className + "],field[" + field.getName() + "],fieldType[" + fieldType.getName() + "] not support");
                    }
                    offsetFieldInfo.setFieldType(type);
                    return offsetFieldInfo;
                } catch (NoSuchFieldException e) {
                    throw BaseRuntimeException.getException(e);
                }
            }).collect(Collectors.toList());
            packetInfo.setOffsetFieldInfoList(offsetFieldInfoList);
        }
        return packetInfo;
    }

    public ParserContext withByteFieldParser(FieldParser<Byte> parser){
        this.byteFieldParser=parser;
        return this;
    }

    public ParserContext withByteArrayFieldParser(FieldParser<byte[]> parser){
        this.byteArrayFieldParser=parser;
        return this;
    }

    public ParserContext withDateFieldParser(FieldParser<Date> parser){
        this.dateFieldParser=parser;
        return this;
    }

    public ParserContext withIntegerFieldParser(FieldParser<Integer> parser){
        this.integerFieldParser=parser;
        return this;
    }

    public ParserContext withIntegerArrayFieldParser(FieldParser<int[]> parser){
        this.integerArrayFieldParser =parser;
        return this;
    }

    public ParserContext withLongFieldParser(FieldParser<Long> parser){
        this.longFieldParser=parser;
        return this;
    }

    public ParserContext withLongArrayFieldParser(FieldParser<long[]> parser){
        this.longArrayFieldParser=parser;
        return this;
    }

    public ParserContext withShortFieldParser(FieldParser<Short> parser){
        this.shortFieldParser=parser;
        return this;
    }

    public ParserContext withShortArrayFieldParser(FieldParser<short[]> parser){
        this.shortArrayFieldParser=parser;
        return this;
    }

    public ParserContext withStringFieldParser(FieldParser<String> parser){
        this.stringFieldParser=parser;
        return this;
    }

    public ParserContext withEnableOffsetField(boolean enableOffsetField){
        this.enableOffsetField=enableOffsetField;
        return this;
    }

    public boolean isEnableOffsetField() {
        return enableOffsetField;
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

    public FieldParser<ByteBuf> getByteBufFieldParser() {
        return byteBufFieldParser;
    }

    public FieldParser[] getFieldParserArr() {
        return fieldParserArr;
    }

    public Map<Class, FieldParser> getClassToParser() {
        return classToParser;
    }
}
