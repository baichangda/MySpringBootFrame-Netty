package com.bcd.nettyserver.http.define;

import com.bcd.nettyserver.http.data.NettyHttpRequestMethod;
import com.bcd.nettyserver.http.result.NettyHttpDeferredResult;
import com.bcd.nettyserver.http.result.handler.NettyHttpResultHandler;
import com.bcd.nettyserver.http.result.handler.impl.NettyHttpResultHandlerImpl;
import io.netty.handler.codec.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommonConst {
    /**
     * 请求uri和方法对应的map
     */
    public final static Map<String,Map<String,NettyHttpRequestMethod>> SERVER_ID_TO_REQUEST_METHOD_MAP_MAP=new HashMap<>();
    /**
     * 自定义method和netty method的对应关系
     */
    public final static Map<HttpMethod,NettyHttpRequestMethodEnum> NETTY_METHOD_TO_MY_NETTY_METHOD =new HashMap<>();
    public final static Map<NettyHttpRequestMethodEnum,HttpMethod> MY_NETTY_METHOD_TO_NETTY_METHOD =new HashMap<>();
    static{
        NETTY_METHOD_TO_MY_NETTY_METHOD.put(HttpMethod.GET, NettyHttpRequestMethodEnum.GET);
        NETTY_METHOD_TO_MY_NETTY_METHOD.put(HttpMethod.POST, NettyHttpRequestMethodEnum.POST);
        MY_NETTY_METHOD_TO_NETTY_METHOD.put(NettyHttpRequestMethodEnum.GET,HttpMethod.GET);
        MY_NETTY_METHOD_TO_NETTY_METHOD.put(NettyHttpRequestMethodEnum.POST,HttpMethod.POST);
    }

    /**
     * 存储全局请求和延迟结果集的map
     * 请求标识符-延迟结果集
     */
    public final static ConcurrentHashMap<String,NettyHttpDeferredResult> NETTY_DEFERRED_RESULT_MAP=new ConcurrentHashMap<>();

    /**
     * 默认的NettyDeferredResult Handler
     */
    public final static NettyHttpResultHandler HTTP_NETTY_RESULT_HANDLER=new NettyHttpResultHandlerImpl();
    /**
     * netty 工作线程组
     */
    public final static ExecutorService HTTP_REQUEST_HANDLE_THREAD_POOL= Executors.newFixedThreadPool(5);
}
