package com.bcd.protocol.gb32960.parse;

import com.bcd.parser.process.FieldProcessor;
import com.bcd.parser.Parser;
import com.bcd.protocol.gb32960.data.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component("parser_32960")
public class GB32960Parser extends Parser implements ApplicationListener<ContextRefreshedEvent> {

    static Logger logger= LoggerFactory.getLogger(GB32960Parser.class);

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
//        parser.setEnableOffsetField(true);
        parser.init();
        String data="232303FE4C534A4132343033304853313932393639010135" +
                "1403190F0507010203010000000469B00EE5271055020F1FFF000002010103424E1E4E2045FFFF2710050006BE437001CF306A060160FFFF0101FFFF0118FF01010E070000000000000000000801010EE527100060000160FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF09010100180EFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFED";
        testPerformance(data,parser);

    }

    private static void testPerformance(String data,Parser parser){
        int core=Runtime.getRuntime().availableProcessors();
        logger.info("core:{}",core);
        AtomicInteger count=new AtomicInteger(0);
        ThreadPoolExecutor pool= (ThreadPoolExecutor)Executors.newFixedThreadPool(core+1);
        for(int i=0;i<pool.getMaximumPoolSize();i++){
            pool.execute(()->{
                testParse(data, parser,count);
            });
        }
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(()->{
            logger.info("speed/s:{}",count.getAndSet(0)/3);
        },3,3,TimeUnit.SECONDS);

        try {
            pool.awaitTermination(1,TimeUnit.HOURS);
        } catch (InterruptedException e) {
            logger.error("interrupted",e);
        }
    }

    private static void testParse(String data,Parser parser,AtomicInteger count){
        byte [] bytes= ByteBufUtil.decodeHexDump(data);
        ByteBuf byteBuf= Unpooled.wrappedBuffer(bytes);
        byteBuf.markReaderIndex();
        byteBuf.markWriterIndex();
        for(int i=1;i<=100000000;i++) {
            byteBuf.resetReaderIndex();
            byteBuf.resetWriterIndex();
            parser.parse(Packet.class,byteBuf);
            count.incrementAndGet();
        }
    }

    private static void testDeParse(String data,Parser parser,AtomicInteger count){
        byte [] bytes= ByteBufUtil.decodeHexDump(data);
        ByteBuf byteBuf= Unpooled.wrappedBuffer(bytes);
        Packet packet= parser.parse(Packet.class, byteBuf);
        ByteBuf res= Unpooled.buffer(bytes.length);
        res.markReaderIndex();
        res.markWriterIndex();
        for(int i=1;i<=1000000;i++) {
            res.resetReaderIndex();
            res.resetWriterIndex();
            parser.deParse(packet,res);
            count.incrementAndGet();
        }
    }


}
