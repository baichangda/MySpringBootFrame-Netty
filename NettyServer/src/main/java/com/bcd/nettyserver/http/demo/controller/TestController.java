package com.bcd.nettyserver.http.demo.controller;

import com.bcd.base.message.JsonMessage;
import com.bcd.nettyserver.http.anno.NettyHttpController;
import com.bcd.nettyserver.http.anno.NettyHttpRequestMapping;
import com.bcd.nettyserver.http.anno.NettyHttpRequestParam;
import com.bcd.nettyserver.http.data.NettyHttpRequestData;
import com.bcd.nettyserver.http.define.NettyHttpRequestMethodEnum;
import com.bcd.nettyserver.http.result.NettyHttpDeferredResult;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("unchecked")
@NettyHttpController("test")
@NettyHttpRequestMapping("/test")
public class TestController {
    private ExecutorService es= Executors.newCachedThreadPool();
    @NettyHttpRequestMapping(value = "/testFun",method = NettyHttpRequestMethodEnum.POST,timeout = 5*1000)
    public NettyHttpDeferredResult test(
            @NettyHttpRequestParam(value = "name") String name,
            @NettyHttpRequestParam(value = "age")Integer age,
            @NettyHttpRequestParam(value = "time")long time,
            NettyHttpRequestData nettyHttpRequestData
    ){
        NettyHttpDeferredResult result= new NettyHttpDeferredResult(5*1000L, nettyHttpRequestData);
        es.execute(()->{
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            result.setResult(JsonMessage.success("测试成功"));
        });
        result.onTimeout(()->{
            result.setErrorResult("测试超时错误结果!");
        });
        return result;
    }
}
