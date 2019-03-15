package com.bcd.protocol.gb32960.parse.impl;

import com.bcd.nettyserver.tcp.parse.Parser;
import com.bcd.nettyserver.tcp.parse.impl.IntegerFieldParser;
import com.bcd.nettyserver.tcp.parse.impl.ShortFieldParser;
import com.bcd.protocol.gb32960.data.*;
import com.bcd.nettyserver.tcp.parse.FieldHandler;
import com.bcd.nettyserver.tcp.util.ParseUtil;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
@Component
public class VehicleCommonDataFieldHandler implements FieldHandler<VehicleCommonData> {
    Logger logger= LoggerFactory.getLogger(VehicleCommonDataFieldHandler.class);

    @Autowired
    @Qualifier("parser_32960")
    Parser parser;
    @Override
    public VehicleCommonData handle(ByteBuf data,Object ...ext) {
        return parseVehicleData(data);
    }

    private VehicleCommonData parseVehicleData(ByteBuf byteBuf){
        VehicleCommonData vehicleCommonData=new VehicleCommonData();
        A:while(byteBuf.isReadable()) {
            byte[] b2=new byte[1];
            byteBuf.readBytes(b2);
            short flag = ShortFieldParser.INSTANCE.parse(b2);
            switch (flag) {
                case 1: {
                    //2.1、整车数据
                    VehicleBaseData data = parser.parse(VehicleBaseData.class,byteBuf);
                    vehicleCommonData.setVehicleBaseData(data);
                    break;
                }
                case 2: {
                    //2.2、驱动电机数据
                    VehicleMotorData vehicleMotorData= parser.parse(VehicleMotorData.class,byteBuf);
                    vehicleCommonData.setVehicleMotorData(vehicleMotorData);
                    break;
                }
                case 3: {
                    //2.3、燃料电池数据
                    VehicleFuelBatteryData vehicleFuelBatteryData= parser.parse(VehicleFuelBatteryData.class,byteBuf);
                    vehicleCommonData.setVehicleFuelBatteryData(vehicleFuelBatteryData);
                    break;
                }
                case 4: {
                    //2.4、发动机数据
                    VehicleEngineData data= parser.parse(VehicleEngineData.class,byteBuf);
                    vehicleCommonData.setVehicleEngineData(data);
                    break;
                }
                case 5: {
                    //2.5、车辆位置数据
                    VehiclePositionData data= parser.parse(VehiclePositionData.class,byteBuf);
                    vehicleCommonData.setVehiclePositionData(data);
                    break;
                }
                case 6: {
                    //2.6、极值数据
                    VehicleLimitValueData data= parser.parse(VehicleLimitValueData.class,byteBuf);
                    vehicleCommonData.setVehicleLimitValueData(data);
                    break;
                }
                case 7: {
                    //2.7、报警数据
                    VehicleAlarmData vehicleAlarmData= parser.parse(VehicleAlarmData.class,byteBuf);
                    vehicleCommonData.setVehicleAlarmData(vehicleAlarmData);
                    break;
                }
                case 8:{
                    //2.8、可充电储能装置电压数据
                    VehicleStorageVoltageData vehicleStorageVoltageData= parser.parse(VehicleStorageVoltageData.class,byteBuf);
                    vehicleCommonData.setVehicleStorageVoltageData(vehicleStorageVoltageData);
                    break;
                }
                case 9:{
                    //2.9、可充电储能装置温度数据
                    VehicleStorageTemperatureData vehicleStorageTemperatureData= parser.parse(VehicleStorageTemperatureData.class,byteBuf);
                    vehicleCommonData.setVehicleStorageTemperatureData(vehicleStorageTemperatureData);
                    break;
                }
                default:{
                    logger.warn("Parse Vehicle Common Data Interrupted,Unknown Flag["+flag+"]");
                    //2.8、如果是自定义数据,只做展现,不解析
                    //2.8.1、解析长度
                    byte[] lenData=new byte[2];
                    byteBuf.getBytes(0,lenData);
                    Integer len= IntegerFieldParser.INSTANCE.parse(lenData);
                    //2.8.2、获取接下来的报文
                    byte[] content=new byte[len];
                    byteBuf.getBytes(0,content);
                    break A;
//                      throw BaseRuntimeException.getException("Parse Vehicle Data Failed,Unknown Flag["+flag+"]");
                }
            }
        }
        return vehicleCommonData;
    }
}
