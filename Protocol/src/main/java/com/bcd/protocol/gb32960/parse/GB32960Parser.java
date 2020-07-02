package com.bcd.protocol.gb32960.parse;

import com.bcd.nettyserver.tcp.info.FieldInfo;
import com.bcd.nettyserver.tcp.info.PacketInfo;
import com.bcd.nettyserver.tcp.parse.FieldParseContext;
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
//        long t1=System.currentTimeMillis();
        FieldParseContext fieldParseContext1= new FieldParseContext();
        FieldInfo fieldInfo=new FieldInfo();
        fieldInfo.setPacketField_singleLen(1);
        fieldParseContext1.setFieldInfo(fieldInfo);
//        for(int i=1;i<=10000000;i++) {
//            byteBuf.resetReaderIndex();
//            byteBuf.resetWriterIndex();
//            test1(byteBuf);
//            test2(byteBuf,context);
//            test3(byteBuf,context,fieldParseContext1);
//        }
        long t2=System.currentTimeMillis();
        for(int i=1;i<=1000000;i++) {
            byteBuf.resetReaderIndex();
            byteBuf.resetWriterIndex();
//            test1(byteBuf);
            test2(byteBuf,context,packetDataFieldParser);
//            test3(byteBuf,context,fieldParseContext1);
        }
        long t3=System.currentTimeMillis();
//        for(int i=1;i<=10000000;i++) {
//            byteBuf.resetReaderIndex();
//            byteBuf.resetWriterIndex();
//            test1(byteBuf);
//            test2(byteBuf,context);

//            test3(byteBuf,context,fieldParseContext1);
//        }
//        long t4=System.currentTimeMillis();
//        System.out.println(t2-t1);
        System.out.println(t3-t2);
//        System.out.println(t4-t3);

    }

    private static Packet test1(ByteBuf byteBuf){
        Packet packet=new Packet();

        byte[] header=new byte[2];
        byteBuf.readBytes(header);
        packet.setHeader(header);

        packet.setFlag(byteBuf.readUnsignedByte());

        packet.setReplyFlag(byteBuf.readUnsignedByte());

        byte[] vinBytes=new byte[17];
        byteBuf.readBytes(vinBytes);
        int discardLen=0;
        for(int i=vinBytes.length-1;i>=0;i--){
            if(vinBytes[i]==0){
                discardLen++;
            }else{
                break;
            }
        }
        packet.setVin(new String(vinBytes,0,vinBytes.length-discardLen));

        packet.setEncodeWay(byteBuf.readUnsignedByte());

        packet.setContentLength(byteBuf.readUnsignedShort());

        ByteBuf content=Unpooled.buffer(packet.getContentLength());
        byteBuf.readBytes(content);
        packet.setDataContent(content);

        packet.setCode(byteBuf.readByte());

        return packet;
    }

    private static Packet test3(ByteBuf byteBuf,ParserContext context,FieldParseContext fieldParseContext1)throws Exception{
        Packet packet= null;
//        try {
//            packet = Packet.class.newInstance();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//        packet.setHeader(context.getByteArrayFieldParser().parse(byteBuf,2,null));
//
//        packet.setFlag(context.getShortFieldParser().parse(byteBuf,1,null));
//
//        packet.setReplyFlag(context.getShortFieldParser().parse(byteBuf,1,null));
//
//        packet.setVin(context.getStringFieldParser().parse(byteBuf,17,null));
//
//        packet.setEncodeWay(context.getShortFieldParser().parse(byteBuf,1,null));
//
//        packet.setContentLength(context.getIntegerFieldParser().parse(byteBuf,2,null));
//
//        packet.setDataContent(context.getByteBufFieldParser().parse(byteBuf,packet.getContentLength(),null));
//
//        packet.setCode(context.getByteFieldParser().parse(byteBuf,1,null));

        context.getByteArrayFieldParser().parse(byteBuf,2,null);

        context.getShortFieldParser().parse(byteBuf,1,null);

        context.getShortFieldParser().parse(byteBuf,1,null);

        context.getStringFieldParser().parse(byteBuf,17,null);

        context.getShortFieldParser().parse(byteBuf,1,null);

        context.getIntegerFieldParser().parse(byteBuf,2,null);

        context.getByteBufFieldParser().parse(byteBuf,309,null);

        context.getByteFieldParser().parse(byteBuf,1,null);

        return packet;
    }

    private static Packet test2(ByteBuf byteBuf,ParserContext context,PacketDataFieldParser packetDataFieldParser) throws Exception{
        Packet packet= context.parse(Packet.class, byteBuf,0);
        PacketData packetData = packetDataFieldParser.parse(packet.getDataContent(),packet.getFlag(),packet.getContentLength());
//            String hex=context.toHex(packetData);
//            packet.setDataContent(Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(hex)));
//            System.out.println(data.toUpperCase());
//            System.out.println(context.toHex(packet).toUpperCase());
        return packet;
    }


}
