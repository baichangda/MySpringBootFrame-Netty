package com.bcd.protocol.gb32960.parse.impl;

import com.bcd.nettyserver.tcp.parse.Parser;
import com.bcd.protocol.gb32960.data.*;
import com.bcd.nettyserver.tcp.parse.FieldHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.springframework.stereotype.Component;

@Component
public class PacketDataFieldHandler implements FieldHandler<PacketData> {
    Parser parser;

    public PacketDataFieldHandler() {
    }


    @Override
    public void setParser(Parser parser) {
        this.parser=parser;
    }

    @Override
    public PacketData handle(ByteBuf data,Object ... ext) {
        Packet packet=(Packet)ext[0];
        PacketData packetData=null;
        switch (packet.getFlag()){
            //车辆登入
            case 1:{
                VehicleLoginData vehicleLoginData= parser.parse(VehicleLoginData.class, data);
                packetData=vehicleLoginData;
                break;
            }
            //车辆实时信息
            case 2:{
                VehicleRealData vehicleRealData= parser.parse(VehicleRealData.class,data);
                packetData=vehicleRealData;
                break;
            }
            //补发信息上报
            case 3:{
                VehicleSupplementData vehicleSupplementData= parser.parse(VehicleSupplementData.class,data);
                packetData=vehicleSupplementData;
                break;
            }
            //车辆登出
            case 4:{
                VehicleLogoutData vehicleLogoutData= parser.parse(VehicleLogoutData.class,data);
                packetData=vehicleLogoutData;
                break;
            }
            //平台登入
            case 5:{
                PlatformLoginData platformLoginData= parser.parse(PlatformLoginData.class,data);
                packetData=platformLoginData;
                break;
            }
            //平台登出
            case 6:{
                PlatformLogoutData platformLogoutData= parser.parse(PlatformLogoutData.class,data);
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
    public String toHex(PacketData data, Object... ext) {
        Packet packet=(Packet)ext[0];
        String hex=null;
        switch (packet.getFlag()){
            //车辆登入
            case 1:{
                hex= parser.toHex(data);
                break;
            }
            //车辆实时信息
            case 2:{
                hex= parser.toHex(data);
                break;
            }
            //补发信息上报
            case 3:{
                hex= parser.toHex(data);
                break;
            }
            //车辆登出
            case 4:{
                hex= parser.toHex(data);
                break;
            }
            //平台登入
            case 5:{
                hex= parser.toHex(data);
                break;
            }
            //平台登出
            case 6:{
                hex= parser.toHex(data);
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
