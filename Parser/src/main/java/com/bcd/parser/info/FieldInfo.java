package com.bcd.parser.info;

import com.bcd.parser.anno.PacketField;

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
     * {@link com.bcd.parser.process.Processor#fieldProcessors} 索引
     * 0、byte/Byte
     * 1、short/Short
     * 2、int/Integer
     * 3、long/Long
     * 4、byte[]
     * 5、short[]
     * 6、int[]
     * 7、long[]
     * 8、String
     * 9、Date
     * 10、ByteBuf
     * 11、List
     *
     * >11、代表自定义的类型
     *
     */
    public int processorIndex;

    /**
     * {@link PacketField#var()} 属性不为空
     * 只有当
     * {@link FieldInfo#processorIndex} 为数字类型(0、1、2、3)时候,才可能是true
     */
    public boolean isVar;

    /**
     * processorIndex=11时候代表集合泛型
     * processorIndex>11代表实体类型
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

    public int getProcessorIndex() {
        return processorIndex;
    }

    public void setProcessorIndex(int processorIndex) {
        this.processorIndex = processorIndex;
    }
}
