package com.bcd.nettyserver.tcp.info;

import com.bcd.nettyserver.tcp.anno.PacketField;

import java.lang.reflect.Field;
import java.util.List;

public class FieldInfo {

    public Field field;

    /**
     * #{@link PacketField} 属性
     */
    public int packetField_index;
    public int packetField_len;
    public char packetField_var;
    public String packetField_lenExpr;
    public String packetField_listLenExpr;
    public int packetField_singleLen;
    public Class packetField_parserClass;



    /**
     * 1、byte/Byte
     * 2、short/Short
     * 3、int/Integer
     * 4、long/Long
     * 5、String
     * 6、Date
     *
     * 7、byte[]
     * 8、short[]
     * 9、int[]
     * 10、long[]
     *
     * 11、ByteBuf
     *
     * 100、实体类型,例如TestBean
     * 101、集合实体类型,例如List<TestBean>
     *
     * 0、通用解析方法失效、此时采用对应{@link PacketField#parserClass()}处理类处理
     *
     */
    public int type;

    /**
     * {@link PacketField#var()} 属性不为空
     * 只有当
     * {@link FieldInfo#type} 为数字类型(1、2、3、4)时候,才可能是true
     */
    public boolean isVar;

    /**
     * 当type=100/101时候才有值,为实体类型
     * type=100,代表实体类型
     * type=101,代表集合泛型
     * type=0,代表{@link PacketField#parserClass()}处理类类型
     */
    public Class clazz;

    /**
     * 长度的逆波兰表达式
     * rpns[0] 对应 {@link PacketField#lenExpr()}表达式
     * rpns[1] 对应 {@link PacketField#listLenExpr()}表达式
     */
    public List[] rpns;





    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public List[] getRpns() {
        return rpns;
    }

    public void setRpns(List[] rpns) {
        this.rpns = rpns;
    }

    public boolean isVar() {
        return isVar;
    }

    public void setVar(boolean var) {
        isVar = var;
    }

    public int getPacketField_index() {
        return packetField_index;
    }

    public int getPacketField_len() {
        return packetField_len;
    }

    public char getPacketField_var() {
        return packetField_var;
    }

    public String getPacketField_lenExpr() {
        return packetField_lenExpr;
    }

    public String getPacketField_listLenExpr() {
        return packetField_listLenExpr;
    }

    public int getPacketField_singleLen() {
        return packetField_singleLen;
    }

    public Class getPacketField_parserClass() {
        return packetField_parserClass;
    }

    public void setPacketField_index(int packetField_index) {
        this.packetField_index = packetField_index;
    }

    public void setPacketField_len(int packetField_len) {
        this.packetField_len = packetField_len;
    }

    public void setPacketField_var(char packetField_var) {
        this.packetField_var = packetField_var;
    }

    public void setPacketField_lenExpr(String packetField_lenExpr) {
        this.packetField_lenExpr = packetField_lenExpr;
    }

    public void setPacketField_listLenExpr(String packetField_listLenExpr) {
        this.packetField_listLenExpr = packetField_listLenExpr;
    }

    public void setPacketField_singleLen(int packetField_singleLen) {
        this.packetField_singleLen = packetField_singleLen;
    }

    public void setPacketField_parserClass(Class packetField_parserClass) {
        this.packetField_parserClass = packetField_parserClass;
    }

}
