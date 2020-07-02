package com.bcd.protocol.gb32960.data;

import com.bcd.nettyserver.tcp.anno.PacketField;
import com.bcd.nettyserver.tcp.anno.ParseAble;

import java.util.Date;

@ParseAble
public class PlatformLoginData extends PacketData {
    //平台登入时间
    @PacketField(index = 1,len = 6)
    Date collectTime;

    //登入流水号
    @PacketField(index = 2,len = 2)
    int sn;

    //平台用户名
    @PacketField(index = 3,len = 12)
    String username;

    //平台密码
    @PacketField(index = 4,len = 20)
    String password;

    //加密规则
    @PacketField(index = 5,len = 1)
    short encode;

    @Override
    public Date getCollectTime() {
        return collectTime;
    }

    @Override
    public void setCollectTime(Date collectTime) {
        this.collectTime = collectTime;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public short getEncode() {
        return encode;
    }

    public void setEncode(short encode) {
        this.encode = encode;
    }
}
