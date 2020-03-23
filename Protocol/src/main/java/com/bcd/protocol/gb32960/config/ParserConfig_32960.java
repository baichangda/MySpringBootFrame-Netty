package com.bcd.protocol.gb32960.config;

import com.bcd.nettyserver.tcp.info.PacketInfo;
import com.bcd.nettyserver.tcp.parse.Parser;
import com.bcd.protocol.gb32960.data.VehicleAlarmData;
import com.bcd.protocol.gb32960.data.VehicleStorageTemperatureData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParserConfig_32960 {
    @Bean("parser_32960")
    public Parser parser(){
        return new Parser();
    }

    public static void main(String[] args) {
        Parser parser=new Parser();
        PacketInfo packetInfo= parser.toPacketInfo(VehicleStorageTemperatureData.class);
    }
}
