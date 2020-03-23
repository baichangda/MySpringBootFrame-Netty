package com.bcd.protocol.gb32960.parse;

import com.bcd.nettyserver.tcp.info.PacketInfo;
import com.bcd.nettyserver.tcp.parse.Parser;
import com.bcd.protocol.gb32960.data.VehicleAlarmData;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component("parser_32960")
public class GB32960Parser extends Parser implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    protected void initHandler() {
        initHandlerBySpring();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        init();
    }

    public static void main(String[] args) {
        Parser parser= new Parser() {
            @Override
            protected void initHandler() {
                initHandlerByScanClass("com.bcd");
            }
        };
        parser.init();
        PacketInfo packetInfo= parser.toPacketInfo(VehicleAlarmData.class);
    }
}
