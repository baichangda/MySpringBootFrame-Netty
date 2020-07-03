package com.bcd.protocol.gb32960.parse;

import com.bcd.nettyserver.tcp.process.FieldProcessor;
import com.bcd.nettyserver.tcp.process.Processor;
import com.bcd.protocol.gb32960.data.Packet;
import com.bcd.protocol.gb32960.data.PacketData;
import com.bcd.protocol.gb32960.parse.impl.PacketDataFieldParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("parser_32960")
public class GB32960Parser extends Processor implements ApplicationListener<ContextRefreshedEvent> {

    public GB32960Parser() {
    }

    @Override
    protected void initPacketInfo() {
        super.initPacketInfoByScanClass("com.bcd");
    }

    @Override
    protected List<FieldProcessor> initExtProcessor() {
        return super.initProcessorByScanClass("com.bcd");
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        init();
    }

    public static void main(String[] args) throws Exception{
        PacketDataFieldParser packetDataFieldParser=new PacketDataFieldParser();
        Processor processor= new Processor() {
            @Override
            protected void initPacketInfo() {
                super.initPacketInfoByScanClass("com.bcd");
            }

            @Override
            protected List<FieldProcessor> initExtProcessor() {
                return super.initProcessorByScanClass("com.bcd");
            }
        };
//        context.withEnableOffsetField(true);
        packetDataFieldParser.setProcessor(processor);
        processor.init();
        String data="232303FE4C534A4132343033304853313932393639010135" +
                "1403190F0507010203010000000469B00EE5271055020F1FFF000002010103424E1E4E2045FFFF2710050006BE437001CF306A060160FFFF0101FFFF0118FF01010E070000000000000000000801010EE527100060000160FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF09010100180EFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFED";
        System.out.println(data.length());
        byte [] bytes= ByteBufUtil.decodeHexDump(data);
        ByteBuf byteBuf= Unpooled.wrappedBuffer(bytes);
//        Packet packet= processor.process(Packet.class, byteBuf);
        byteBuf.markReaderIndex();
        byteBuf.markWriterIndex();
        long t2=System.currentTimeMillis();
        for(int i=1;i<=1000000;i++) {
            byteBuf.resetReaderIndex();
            byteBuf.resetWriterIndex();
            test2(byteBuf,processor,packetDataFieldParser);
//            test3(packet,processor,data);
        }
        long t3=System.currentTimeMillis();

        System.out.println(t3-t2);

    }

    private static Packet test2(ByteBuf byteBuf, Processor processor, PacketDataFieldParser packetDataFieldParser) throws Exception{
        Packet packet= processor.process(Packet.class, byteBuf);
//        PacketData packetData = packetDataFieldParser.parse(packet.getDataContent(),packet.getFlag(),packet.getContentLength());
        return packet;
    }

    private static void test3(Packet packet,Processor processor,String oHex) throws Exception{
        ByteBuf byteBuf=processor.toByteBuf(packet);
//        String hex=ByteBufUtil.hexDump(byteBuf);
//        System.out.println(hex.toUpperCase());
//        System.out.println(oHex.toUpperCase());
    }


}
