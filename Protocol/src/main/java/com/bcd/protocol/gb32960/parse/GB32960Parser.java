package com.bcd.protocol.gb32960.parse;

import com.bcd.nettyserver.tcp.info.PacketInfo;
import com.bcd.nettyserver.tcp.parse.Parser;
import com.bcd.protocol.gb32960.data.Packet;
import com.bcd.protocol.gb32960.data.PacketData;
import com.bcd.protocol.gb32960.data.VehicleAlarmData;
import com.bcd.protocol.gb32960.parse.impl.PacketDataFieldHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
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
        PacketDataFieldHandler packetDataFieldHandler=new PacketDataFieldHandler();
        Parser parser= new Parser() {
            @Override
            protected void initHandler() {
                initHandlerByScanClass("com.bcd");
            }
        };
        parser.init();
        packetDataFieldHandler.setParser(parser);
        String data="232303FE4C534A4132343033304853313932393639010135" +
                "1403190F0507010203010000000469B00EE5271055020F1FFF000002010103424E1E4E2045FFFF2710050006BE437001CF306A060160FFFF0101FFFF0118FF01010E070000000000000000000801010EE527100060000160FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF09010100180EFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFED";
        System.out.println(data.length());
        long t1=System.currentTimeMillis();
        for(int i=1;i<=1000000;i++) {
            ByteBuf byteBuf= Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(data));
            Packet packet = parser.parse(Packet.class, byteBuf);
            byte[] content = packet.getDataContent();
            ByteBuf contentByteBuf = Unpooled.wrappedBuffer(content);
            PacketData packetData = packetDataFieldHandler.handle(contentByteBuf, packet);
//            String hex=parser.toHex(packetData);
//            System.out.println(hex);
//            System.out.println(data.contains(hex.toUpperCase()));
            byteBuf.release();
        }
        long t2=System.currentTimeMillis();
        System.out.println(t2-t1);

    }
}
