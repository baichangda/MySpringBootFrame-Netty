package com.bcd.protocol.gb32960.parse;

import com.bcd.nettyserver.tcp.parse.ParserContext;
import com.bcd.protocol.gb32960.data.Packet;
import com.bcd.protocol.gb32960.data.PacketData;
import com.bcd.protocol.gb32960.parse.impl.PacketDataFieldParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component("parser_32960")
public class GB32960Parser extends ParserContext implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    protected void initHandler() {
        initHandlerBySpring();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        init();
    }

    public static void main(String[] args) {
        PacketDataFieldParser packetDataFieldParser=new PacketDataFieldParser();
        ParserContext context= new ParserContext() {
            @Override
            protected void initHandler() {
                initHandlerByScanClass("com.bcd");
            }
        };
        context.init();
        packetDataFieldParser.setContext(context);
        String data="232303FE4C534A4132343033304853313932393639010135" +
                "1403190F0507010203010000000469B00EE5271055020F1FFF000002010103424E1E4E2045FFFF2710050006BE437001CF306A060160FFFF0101FFFF0118FF01010E070000000000000000000801010EE527100060000160FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF09010100180EFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFED";
        System.out.println(data.length());
        long t1=System.currentTimeMillis();
        byte [] bytes= ByteBufUtil.decodeHexDump(data);
//        for(int i=1;i<=1000000;i++) {
            ByteBuf byteBuf= Unpooled.wrappedBuffer(bytes);
            Packet packet = context.parse(Packet.class, byteBuf);
            PacketData packetData = packetDataFieldParser.parse(packet.getDataContent(),packet.getDataContent().readableBytes(), packet);
//            String hex=context.toHex(packetData);
//            System.out.println(hex);
//            System.out.println(data.contains(hex.toUpperCase()));
//        }
        long t2=System.currentTimeMillis();
        System.out.println(t2-t1);

    }
}
