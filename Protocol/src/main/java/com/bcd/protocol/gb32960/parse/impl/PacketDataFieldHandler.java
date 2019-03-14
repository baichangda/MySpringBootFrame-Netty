package com.bcd.protocol.gb32960.parse.impl;

import com.bcd.protocol.gb32960.data.*;
import com.bcd.nettyserver.tcp.parse.FieldHandler;
import com.bcd.nettyserver.tcp.util.ParseUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.springframework.stereotype.Component;

@Component
public class PacketDataFieldHandler implements FieldHandler<PacketData> {
    @Override
    public PacketData handle(ByteBuf data,Object ... ext) {
        Packet packet=(Packet)ext[0];
        int len=packet.getContentLength();
        ByteBuf content= Unpooled.buffer(len,len);
        data.readBytes(content,len);
        PacketData packetData=null;
        switch (packet.getFlag()){
            //车辆登入
            case 1:{
                VehicleLoginData vehicleLoginData= ParseUtil.parse(VehicleLoginData.class, content);
                packetData=vehicleLoginData;
                break;
            }
            //车辆实时信息
            case 2:{
                VehicleRealData vehicleRealData= ParseUtil.parse(VehicleRealData.class,content);
                packetData=vehicleRealData;
                break;
            }
            //补发信息上报
            case 3:{
                VehicleSupplementData vehicleSupplementData= ParseUtil.parse(VehicleSupplementData.class,content);
                packetData=vehicleSupplementData;
                break;
            }
            //车辆登出
            case 4:{
                VehicleLogoutData vehicleLogoutData= ParseUtil.parse(VehicleLogoutData.class,content);
                packetData=vehicleLogoutData;
                break;
            }
            //平台登入
            case 5:{
                PlatformLoginData platformLoginData= ParseUtil.parse(PlatformLoginData.class,content);
                packetData=platformLoginData;
                break;
            }
            //平台登出
            case 6:{
                PlatformLogoutData platformLogoutData= ParseUtil.parse(PlatformLogoutData.class,content);
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
}
