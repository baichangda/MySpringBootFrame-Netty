package com.bcd.nettyserver.tcp.info;

import com.bcd.nettyserver.tcp.anno.OffsetField;

import java.lang.reflect.Field;
import java.util.List;

public class OffsetFieldInfo {
    Field field;

    Field sourceField;

    /**
     * #{@link OffsetField} 属性
     */
    String offsetField_sourceField;
    String offsetField_expr;

    List<String> rpn;

    /**
     * 字段类型
     * 1、byte/Byte
     * 2、short/Short
     * 3、int/Integer
     * 4、long/Long
     * 5、float/Float
     * 6、double/Double
     */
    int fieldType;

    public Field getSourceField() {
        return sourceField;
    }

    public void setSourceField(Field sourceField) {
        this.sourceField = sourceField;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getOffsetField_sourceField() {
        return offsetField_sourceField;
    }

    public void setOffsetField_sourceField(String offsetField_sourceField) {
        this.offsetField_sourceField = offsetField_sourceField;
    }

    public String getOffsetField_expr() {
        return offsetField_expr;
    }

    public void setOffsetField_expr(String offsetField_expr) {
        this.offsetField_expr = offsetField_expr;
    }

    public List<String> getRpn() {
        return rpn;
    }

    public void setRpn(List<String> rpn) {
        this.rpn = rpn;
    }

    public int getFieldType() {
        return fieldType;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    public static void main(String[] args){
        long l=0xffffffffL;
        System.out.println(l&0xfffffff5);
        System.out.println(0xfffffff5);
    }
}
