package com.bcd.protocol.gb32960.parse.impl;

import com.bcd.nettyserver.tcp.process.FieldDeProcessContext;
import com.bcd.nettyserver.tcp.process.FieldProcessContext;
import com.bcd.nettyserver.tcp.process.FieldProcessor;
import com.bcd.protocol.gb32960.data.*;
import io.netty.buffer.ByteBuf;
import org.springframework.stereotype.Component;


@Component
public class PacketDataFieldParser extends FieldProcessor<PacketData> {

    public PacketDataFieldParser() {
    }

    public PacketData parse(ByteBuf data,int flag,FieldProcessContext processContext){
        PacketData packetData=null;
        switch (flag){
            //车辆登入
            case 1:{
                VehicleLoginData vehicleLoginData= processor.process(VehicleLoginData.class, data,processContext);
                packetData=vehicleLoginData;
                break;
            }
            //车辆实时信息
            case 2:{
                VehicleRealData vehicleRealData= processor.process(VehicleRealData.class,data,processContext);
                packetData=vehicleRealData;
                break;
            }
            //补发信息上报
            case 3:{
                VehicleSupplementData vehicleSupplementData= processor.process(VehicleSupplementData.class,data,processContext);
                packetData=vehicleSupplementData;
                break;
            }
            //车辆登出
            case 4:{
                VehicleLogoutData vehicleLogoutData= processor.process(VehicleLogoutData.class,data,processContext);
                packetData=vehicleLogoutData;
                break;
            }
            //平台登入
            case 5:{
                PlatformLoginData platformLoginData= processor.process(PlatformLoginData.class,data,processContext);
                packetData=platformLoginData;
                break;
            }
            //平台登出
            case 6:{
                PlatformLogoutData platformLogoutData= processor.process(PlatformLogoutData.class,data,processContext);
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
    public boolean support(Class clazz) {
        return clazz==PacketData.class;
    }

    @Override
    public PacketData process(ByteBuf data, FieldProcessContext processContext) {
        Packet packet=(Packet)processContext.getInstance();
        return parse(data,packet.getFlag(),processContext);
    }

    @Override
    public void deProcess(PacketData data, ByteBuf dest, FieldDeProcessContext processContext) {
        int flag=((Packet)processContext.getInstance()).getFlag();
        switch (flag){
            //车辆登入
            case 1:{
                processor.deProcess(data,dest,processContext);
                break;
            }
            //车辆实时信息
            case 2:{
                processor.deProcess(data,dest,processContext);
                break;
            }
            //补发信息上报
            case 3:{
                processor.deProcess(data,dest,processContext);
                break;
            }
            //车辆登出
            case 4:{
                processor.deProcess(data,dest,processContext);
                break;
            }
            //平台登入
            case 5:{
                processor.deProcess(data,dest,processContext);
                break;
            }
            //平台登出
            case 6:{
                processor.deProcess(data,dest,processContext);
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
