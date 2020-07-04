package com.bcd.protocol.gb32960.parse;

import com.bcd.parser.process.FieldProcessor;
import com.bcd.parser.Parser;
import com.bcd.protocol.gb32960.data.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("parser_32960")
public class GB32960Parser extends Parser implements ApplicationListener<ContextRefreshedEvent> {

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
        Parser parser= new Parser() {
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
        parser.init();
        String data="232303FE4C534A4132343033304853313932393639010135" +
                "1403190F0507010203010000000469B00EE5271055020F1FFF000002010103424E1E4E2045FFFF2710050006BE437001CF306A060160FFFF0101FFFF0118FF01010E070000000000000000000801010EE527100060000160FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF09010100180EFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFED";
        System.out.println(data.length());
        byte [] bytes= ByteBufUtil.decodeHexDump(data);
        ByteBuf byteBuf= Unpooled.wrappedBuffer(bytes);
        Packet packet= parser.parse(Packet.class, byteBuf);
        byteBuf.markReaderIndex();
        byteBuf.markWriterIndex();
        long t2=System.currentTimeMillis();
        for(int i=1;i<=1000000;i++) {
            byteBuf.resetReaderIndex();
            byteBuf.resetWriterIndex();
//            test2(byteBuf,parser);
            test3(packet,parser,data);
        }
        long t3=System.currentTimeMillis();

        System.out.println(t3-t2);

    }

    private static Packet test2(ByteBuf byteBuf, Parser parser){
        Packet packet= parser.parse(Packet.class, byteBuf);
        return packet;
    }

    private static void test3(Packet packet, Parser parser, String oHex){
        ByteBuf byteBuf=parser.toByteBuf(packet);
//        String hex=ByteBufUtil.hexDump(byteBuf);
//        System.out.println(hex.toUpperCase());
//        System.out.println(oHex.toUpperCase());
    }


}
