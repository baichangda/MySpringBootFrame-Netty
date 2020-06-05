package com.bcd.nettyserver.tcp;

import com.bcd.base.util.SpringUtil;
import com.bcd.nettyserver.tcp.info.FieldInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component("tcpStarter")
public class Starter implements CommandLineRunner{
    Logger logger= LoggerFactory.getLogger(Starter.class);
    ExecutorService POOL;
    @Override
    public void run(String... args) throws Exception {
        Collection<TcpServer> tcpServers= SpringUtil.applicationContext.getBeansOfType(TcpServer.class).values();
        if(!tcpServers.isEmpty()) {
            POOL = Executors.newFixedThreadPool(tcpServers.size());
            tcpServers.forEach(tcpServer -> {
                POOL.execute(tcpServer);
                logger.info("启动netty tcp服务器[" + tcpServer.getPort() + "]!");
            });
        }
    }

    public static void main(String[] args) throws Throwable {
        FieldInfo fieldInfo=new FieldInfo();
        int count=1000000000;
        Field field= FieldInfo.class.getField("type");
        field.setAccessible(true);
        Method method=FieldInfo.class.getMethod("setType",int.class);
        MethodHandle methodHandle= MethodHandles.publicLookup().findSetter(FieldInfo.class,"type",int.class);
        method.setAccessible(true);
        long t1=System.currentTimeMillis();
        for(int i=0;i<count;i++){
//            fieldInfo.type=i;
            fieldInfo.setType(i);
            fieldInfo.getType();
        }
//        System.out.println("===================");

        long t2=System.currentTimeMillis();
        for(int i=0;i<count;i++){
//            field.set(fieldInfo,i);
            method.invoke(fieldInfo,i);
            fieldInfo.getType();
        }

        long t3=System.currentTimeMillis();

        for(int i=0;i<count;i++){
//            field.set(fieldInfo,i);
            methodHandle.invokeExact(fieldInfo,i);
            fieldInfo.getType();
        }

        long t4=System.currentTimeMillis();
        System.out.println((t2-t1)+"  "+(t3-t2)+"  "+(t4-t3));
    }
}
