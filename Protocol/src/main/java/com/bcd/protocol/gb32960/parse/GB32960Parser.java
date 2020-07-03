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

    public GB32960Parser() {
        super("com.bcd");
    }

    @Override
    protected void initHandler() {
        initHandlerBySpring();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        init();
    }

    public static void main(String[] args) throws Exception{
        PacketDataFieldParser packetDataFieldParser=new PacketDataFieldParser();
        ParserContext context= new ParserContext("com.bcd") {
            @Override
            protected void initHandler() {
                initParserByScanClass();
            }
        };
//        context.withEnableOffsetField(true);
        context.init();
        packetDataFieldParser.setContext(context);
        String data="232303FE4C534A4132343033304853313932393639010135" +
                "1403190F0507010203010000000469B00EE5271055020F1FFF000002010103424E1E4E2045FFFF2710050006BE437001CF306A060160FFFF0101FFFF0118FF01010E070000000000000000000801010EE527100060000160FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF09010100180EFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFED";
        System.out.println(data.length());
        byte [] bytes= ByteBufUtil.decodeHexDump(data);
        ByteBuf byteBuf= Unpooled.wrappedBuffer(bytes);
        byteBuf.markReaderIndex();
        byteBuf.markWriterIndex();
        long t2=System.currentTimeMillis();
        for(int i=1;i<=100000;i++) {
            byteBuf.resetReaderIndex();
            byteBuf.resetWriterIndex();
            test2(byteBuf,context,packetDataFieldParser);
        }
        long t3=System.currentTimeMillis();

        System.out.println(t3-t2);

    }

    private static Packet test2(ByteBuf byteBuf,ParserContext context,PacketDataFieldParser packetDataFieldParser) throws Exception{
        Packet packet= context.parse(Packet.class, byteBuf,0);
        PacketData packetData = packetDataFieldParser.parse(packet.getDataContent(),packet.getFlag(),packet.getContentLength());
            String hex=context.toHex(packetData);
//            packet.setDataContent(Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(hex)));
//            System.out.println(data.toUpperCase());
//            System.out.println(context.toHex(packet).toUpperCase());
        return packet;
    }


}
