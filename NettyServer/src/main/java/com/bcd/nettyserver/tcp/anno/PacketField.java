package com.bcd.nettyserver.tcp.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PacketField {
    //序号
    int index();
    //字节长度
    int len() default 0;
    //是否是长度字段(用作于包的最外层的长度字段标记)
    boolean isLengthField() default false;
    //头字段值(用作于包的最外层的头字段标记)
    String headValue() default "";
    //变量名称
    String var() default "";
    //字段字节长度表达式(用于固定长度字段解析,配合var参数使用)
    String lenExpr() default "";
    //集合长度表达式(用于对象集合字段不定长度的解析,配合var参数使用)
    String listLenExpr() default "";
    /**
     * 单个元素字节长度(用于字节数组转换成short、int、long数组或者集合的转换配比)
     * 例如:
     * 原始为 byte[8] 字段数据 转换成 int[],
     * 如果配比为 2: 则表示2个byte转换成一个int存入数组,因为一个int可以代表4个字节,所以int的高两位字节补全部补0,最后转换的长度为 int[4]
     * 如果配比为 4: 则表示4个byte转换成一个int存入数组,最后转换的长度为 int[2]
     */
    int singleLen() default 1;
    //处理类
    Class handleClass() default Void.class;
}
