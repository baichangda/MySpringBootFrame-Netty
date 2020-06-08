package com.bcd.protocol.gb32960.parse.impl;

import com.bcd.nettyserver.tcp.parse.FieldParseContext;
import com.bcd.nettyserver.tcp.parse.FieldParser;
import com.bcd.nettyserver.tcp.parse.FieldToHexContext;
import com.bcd.nettyserver.tcp.parse.ParserContext;
import com.bcd.nettyserver.tcp.parse.impl.IntegerFieldParser;
import com.bcd.nettyserver.tcp.parse.impl.ShortFieldParser;
import com.bcd.protocol.gb32960.data.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
@Component
public class VehicleCommonDataFieldParser implements FieldParser<VehicleCommonData> {
    Logger logger= LoggerFactory.getLogger(VehicleCommonDataFieldParser.class);

    ParserContext context;

    @Override
    public VehicleCommonData parse(ByteBuf data, int len, FieldParseContext fieldParseContext) {
        return parseVehicleData(data,len,fieldParseContext);
    }

    @Override
    public void setContext(ParserContext context) {
        this.context =context;
    }

    private VehicleCommonData parseVehicleData(ByteBuf byteBuf, int len, FieldParseContext fieldParseContext){
        VehicleCommonData vehicleCommonData=new VehicleCommonData();
        int allLen= fieldParseContext.getAllLen()-6;
        int beginLeave=byteBuf.readableBytes();
        A:while(byteBuf.isReadable()) {
            int curLeave=byteBuf.readableBytes();
            if(beginLeave-curLeave>=allLen){
                break;
            }
            short flag = byteBuf.readUnsignedByte();
            switch (flag) {
                case 1: {
                    //2.1、整车数据
                    VehicleBaseData data = context.parse(VehicleBaseData.class,byteBuf);
                    vehicleCommonData.setVehicleBaseData(data);
                    break;
                }
                case 2: {
                    //2.2、驱动电机数据
                    VehicleMotorData vehicleMotorData= context.parse(VehicleMotorData.class,byteBuf);
                    vehicleCommonData.setVehicleMotorData(vehicleMotorData);
                    break;
                }
                case 3: {
                    //2.3、燃料电池数据
                    VehicleFuelBatteryData vehicleFuelBatteryData= context.parse(VehicleFuelBatteryData.class,byteBuf);
                    vehicleCommonData.setVehicleFuelBatteryData(vehicleFuelBatteryData);
                    break;
                }
                case 4: {
                    //2.4、发动机数据
                    VehicleEngineData data= context.parse(VehicleEngineData.class,byteBuf);
                    vehicleCommonData.setVehicleEngineData(data);
                    break;
                }
                case 5: {
                    //2.5、车辆位置数据
                    VehiclePositionData data= context.parse(VehiclePositionData.class,byteBuf);
                    vehicleCommonData.setVehiclePositionData(data);
                    break;
                }
                case 6: {
                    //2.6、极值数据
                    VehicleLimitValueData data= context.parse(VehicleLimitValueData.class,byteBuf);
                    vehicleCommonData.setVehicleLimitValueData(data);
                    break;
                }
                case 7: {
                    //2.7、报警数据
                    VehicleAlarmData vehicleAlarmData= context.parse(VehicleAlarmData.class,byteBuf);
                    vehicleCommonData.setVehicleAlarmData(vehicleAlarmData);
                    break;
                }
                case 8:{
                    //2.8、可充电储能装置电压数据
                    VehicleStorageVoltageData vehicleStorageVoltageData= context.parse(VehicleStorageVoltageData.class,byteBuf);
                    vehicleCommonData.setVehicleStorageVoltageData(vehicleStorageVoltageData);
                    break;
                }
                case 9:{
                    //2.9、可充电储能装置温度数据
                    VehicleStorageTemperatureData vehicleStorageTemperatureData= context.parse(VehicleStorageTemperatureData.class,byteBuf);
                    vehicleCommonData.setVehicleStorageTemperatureData(vehicleStorageTemperatureData);
                    break;
                }
                default:{
                    logger.warn("Parse Vehicle Common Data Interrupted,Unknown Flag["+flag+"]");
                    //2.8、如果是自定义数据,只做展现,不解析
                    //2.8.1、解析长度
                    int dataLen= byteBuf.readUnsignedShort();
                    //2.8.2、获取接下来的报文
                    byte[] content=new byte[dataLen];
                    byteBuf.getBytes(0,content);
                    break A;
//                      throw BaseRuntimeException.getException("Parse Vehicle Data Failed,Unknown Flag["+flag+"]");
                }
            }
        }
        return vehicleCommonData;
    }

    @Override
    public String toHex(VehicleCommonData data,int len, FieldToHexContext fieldToHexContext) {
        StringBuilder sb=new StringBuilder();
        if(data.getVehicleBaseData()!=null){
            sb.append("01");
            sb.append(context.toHex(data.getVehicleBaseData()));
        }
        if(data.getVehicleMotorData()!=null){
            sb.append("02");
            sb.append(context.toHex(data.getVehicleMotorData()));
        }
        if(data.getVehicleFuelBatteryData()!=null){
            sb.append("03");
            sb.append(context.toHex(data.getVehicleFuelBatteryData()));
        }
        if(data.getVehicleEngineData()!=null){
            sb.append("04");
            sb.append(context.toHex(data.getVehicleEngineData()));
        }
        if(data.getVehiclePositionData()!=null){
            sb.append("05");
            sb.append(context.toHex(data.getVehiclePositionData()));
        }
        if(data.getVehicleLimitValueData()!=null){
            sb.append("06");
            sb.append(context.toHex(data.getVehicleLimitValueData()));
        }
        if(data.getVehicleAlarmData()!=null){
            sb.append("07");
            sb.append(context.toHex(data.getVehicleAlarmData()));
        }
        if(data.getVehicleStorageVoltageData()!=null){
            sb.append("08");
            sb.append(context.toHex(data.getVehicleStorageVoltageData()));
        }
        if(data.getVehicleStorageTemperatureData()!=null){
            sb.append("09");
            sb.append(context.toHex(data.getVehicleStorageTemperatureData()));
        }
        return sb.toString();
    }
}
