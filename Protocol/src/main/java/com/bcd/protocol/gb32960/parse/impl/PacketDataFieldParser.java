package com.bcd.protocol.gb32960.parse.impl;

import com.bcd.nettyserver.tcp.parse.FieldParseContext;
import com.bcd.nettyserver.tcp.parse.FieldParser;
import com.bcd.nettyserver.tcp.parse.FieldToByteBufContext;
import com.bcd.nettyserver.tcp.parse.ParserContext;
import com.bcd.protocol.gb32960.data.*;
import io.netty.buffer.ByteBuf;
import org.springframework.stereotype.Component;

@Component
public class PacketDataFieldParser implements FieldParser<PacketData> {
    ParserContext context;

    public PacketDataFieldParser() {
    }

    @Override
    public void setContext(ParserContext context) {
        this.context =context;
    }

    public PacketData parse(ByteBuf data,int flag,int len){
        PacketData packetData=null;
        switch (flag){
            //车辆登入
            case 1:{
                packetData= context.parse(VehicleLoginData.class, data,len);
                break;
            }
            //车辆实时信息
            case 2:{
                packetData= context.parse(VehicleRealData.class,data,len);
                break;
            }
            //补发信息上报
            case 3:{
                packetData= context.parse(VehicleSupplementData.class,data,len);
                break;
            }
            //车辆登出
            case 4:{
                packetData= context.parse(VehicleLogoutData.class,data,len);
                break;
            }
            //平台登入
            case 5:{
                packetData= context.parse(PlatformLoginData.class,data,len);
                break;
            }
            //平台登出
            case 6:{
                packetData= context.parse(PlatformLogoutData.class,data,len);
                break;
            }
            //心跳
            case 7:{
                break;
            }
            //终端校时
            case 8:{
                break;
            }
        }
        return packetData;
    }

    @Override
    public PacketData parse(ByteBuf data, int len, FieldParseContext fieldParseContext){
        Packet packet=(Packet)fieldParseContext.getInstance();
        return parse(data,packet.getFlag(),len);
    }

    @Override
    public void toByteBuf(PacketData data, int len, FieldToByteBufContext fieldToByteBufContext,ByteBuf result) {
        int flag=data.getFlag();
        switch (flag){
            //车辆登入
            case 1:{
                result.writeBytes(context.toByteBuf(data));
                break;
            }
            //车辆实时信息
            case 2:{
                result.writeBytes(context.toByteBuf(data));
                break;
            }
            //补发信息上报
            case 3:{
                result.writeBytes(context.toByteBuf(data));
                break;
            }
            //车辆登出
            case 4:{
                result.writeBytes(context.toByteBuf(data));
                break;
            }
            //平台登入
            case 5:{
                result.writeBytes(context.toByteBuf(data));
                break;
            }
            //平台登出
            case 6:{
                result.writeBytes(context.toByteBuf(data));
                break;
            }
            //心跳
            case 7:{
                break;
            }
            //终端校时
            case 8:{
                break;
            }
        }
    }
}
