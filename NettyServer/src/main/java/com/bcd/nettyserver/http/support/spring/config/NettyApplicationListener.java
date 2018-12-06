package com.bcd.nettyserver.http.support.spring.config;

import com.bcd.nettyserver.http.anno.NettyHttpController;
import com.bcd.nettyserver.http.data.NettyHttpRequestMethod;
import com.bcd.nettyserver.http.define.CommonConst;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 初始化所有 NettyHttpController 注解的controller 并将其 方法加入全局map中供netty服务器请求匹配
 */
@Component
public class NettyApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<String,Object> nettyControllerMap= event.getApplicationContext().getBeansWithAnnotation(NettyHttpController.class);
        nettyControllerMap.values().stream().flatMap(e-> NettyHttpRequestMethod.generateByNettyController(e).stream()).forEach(
                e -> {
                    String serverId=e.getServerId();
                    Map<String,NettyHttpRequestMethod> map= CommonConst.SERVER_ID_TO_REQUEST_METHOD_MAP_MAP.get(serverId);
                    if(map==null){
                        map=new HashMap<>();
                        CommonConst.SERVER_ID_TO_REQUEST_METHOD_MAP_MAP.put(serverId,map);
                    }
                    map.put(e.getPath(),e);
                }
        );
    }
}
