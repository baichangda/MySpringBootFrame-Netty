package com.bcd.protocol.gb32960.parse.impl;

import com.bcd.nettyserver.tcp.parse.FieldParser;
import com.bcd.nettyserver.tcp.parse.ParserContext;
import com.bcd.protocol.gb32960.data.*;
import io.netty.buffer.ByteBuf;
import org.springframework.stereotype.Component;

@Component
public class PacketDataFieldHandler implements FieldParser<PacketData> {
    ParserContext context;

    public PacketDataFieldHandler() {
    }

    @Override
    public void setContext(ParserContext context) {
        this.context =context;
    }

    @Override
    public PacketData parse(ByteBuf data,int len,Object instance,Object ... ext) {
        Packet packet=(Packet)instance;
        PacketData packetData=null;
        switch (packet.getFlag()){
            //车辆登入
            case 1:{
                VehicleLoginData vehicleLoginData= context.parse(VehicleLoginData.class, data);
                packetData=vehicleLoginData;
                break;
            }
            //车辆实时信息
            case 2:{
                VehicleRealData vehicleRealData= context.parse(VehicleRealData.class,data);
                packetData=vehicleRealData;
                break;
            }
            //补发信息上报
            case 3:{
                VehicleSupplementData vehicleSupplementData= context.parse(VehicleSupplementData.class,data);
                packetData=vehicleSupplementData;
                break;
            }
            //车辆登出
            case 4:{
                VehicleLogoutData vehicleLogoutData= context.parse(VehicleLogoutData.class,data);
                packetData=vehicleLogoutData;
                break;
            }
            //平台登入
            case 5:{
                PlatformLoginData platformLoginData= context.parse(PlatformLoginData.class,data);
                packetData=platformLoginData;
                break;
            }
            //平台登出
            case 6:{
                PlatformLogoutData platformLogoutData= context.parse(PlatformLogoutData.class,data);
                packetData=platformLogoutData;
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
    public String toHex(PacketData data,int len, Object... ext) {
        Packet packet=(Packet)ext[0];
        String hex=null;
        switch (packet.getFlag()){
            //车辆登入
            case 1:{
                hex= context.toHex(data);
                break;
            }
            //车辆实时信息
            case 2:{
                hex= context.toHex(data);
                break;
            }
            //补发信息上报
            case 3:{
                hex= context.toHex(data);
                break;
            }
            //车辆登出
            case 4:{
                hex= context.toHex(data);
                break;
            }
            //平台登入
            case 5:{
                hex= context.toHex(data);
                break;
            }
            //平台登出
            case 6:{
                hex= context.toHex(data);
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
        return hex;
    }
}
