package com.bcd.nettyserver.tcp.info;

import com.bcd.nettyserver.tcp.anno.PacketField;

import java.lang.reflect.Field;
import java.util.List;

public class FieldInfo {
    public Field field;

    public PacketField packetField;

    /**
     * 1、byte/Byte
     * 2、short/Short
     * 3、int/Integer
     * 4、long/Long
     * 5、String
     * 6、Date
     *
     * 7、byte[]/Byte[]
     * 8、short[]/Short[]
     * 9、int[]/Integer[]
     * 10、long[]/Long[]
     *
     * 11、ByteBuf
     *
     * 100、实体类型,例如TestBean
     * 101、集合实体类型,例如List<TestBean>
     *
     * 0、通用解析方法失效、此时采用对应{@link PacketField#handleClass()}处理类处理
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
     * type=0,代表{@link PacketField#handleClass()}处理类类型
     */
    public Class clazz;

    /**
     * 逆波兰表达式
     */
    public List<String>[] rpns;



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

    public PacketField getPacketField() {
        return packetField;
    }

    public void setPacketField(PacketField packetField) {
        this.packetField = packetField;
    }

    public List<String>[] getRpns() {
        return rpns;
    }

    public void setRpns(List<String>[] rpns) {
        this.rpns = rpns;
    }

    public boolean isVar() {
        return isVar;
    }

    public void setVar(boolean var) {
        isVar = var;
    }
}
