package com.bcd.nettyserver.http.result;

import com.bcd.nettyserver.http.data.NettyHttpRequestData;
import com.bcd.nettyserver.http.define.CommonConst;
import com.bcd.nettyserver.http.result.handler.NettyHttpResultHandler;

import java.util.Date;

/**
 * 自定义Netty Http请求延迟结果集
 * @param <T>
 */
public class NettyHttpDeferredResult<T> {

    private final Long startTimestamp;
    private final Long timeout;

    private NettyHttpRequestData nettyHttpRequestData;

    private Runnable timeoutCallback;

    private Runnable completionCallback;

    private NettyHttpResultHandler resultHandler;

    private volatile Object result;
    private volatile boolean expired = false;
    private volatile boolean completed= false;
    private volatile boolean setted=false;

    public NettyHttpDeferredResult(Long timeout, NettyHttpRequestData nettyHttpRequestData) {
        this.timeout = timeout;
        this.startTimestamp=new Date().getTime();
        this.nettyHttpRequestData = nettyHttpRequestData;
        this.resultHandler= CommonConst.HTTP_NETTY_RESULT_HANDLER;
        CommonConst.NETTY_DEFERRED_RESULT_MAP.put(nettyHttpRequestData.getId(), this);
    }

    public NettyHttpRequestData getNettyHttpRequestData() {
        return nettyHttpRequestData;
    }

    public void setNettyHttpRequestData(NettyHttpRequestData nettyHttpRequestData) {
        this.nettyHttpRequestData = nettyHttpRequestData;
    }

    public Runnable getTimeoutCallback() {
        return timeoutCallback;
    }

    public void setTimeoutCallback(Runnable timeoutCallback) {
        this.timeoutCallback = timeoutCallback;
    }

    public Runnable getCompletionCallback() {
        return completionCallback;
    }

    public void setCompletionCallback(Runnable completionCallback) {
        this.completionCallback = completionCallback;
    }

    public Long getStartTimestamp() {
        return startTimestamp;
    }

    public boolean isSetted() {
        return setted;
    }

    public final boolean isExpired() {
        return (this.result == null && this.expired);
    }

    public boolean isCompleted() {
        return completed;
    }


    public final Long getTimeoutValue() {
        return this.timeout;
    }


    public void onTimeout(Runnable callback) {
        this.timeoutCallback = callback;
    }

    public void onCompletion(Runnable callback) {
        this.completionCallback = callback;
    }

    public boolean setResult(T result) {
        return setResultInternal(result);
    }

    public boolean setErrorResult(Object result) {
        return setResultInternal(result);
    }

    private boolean setResultInternal(Object result) {
        //进行值的设置,同时进行response的返回
        if (isSetted()) {
            return false;
        }
        NettyHttpResultHandler resultHandlerToUse;
        synchronized (this) {
            if (isSetted()) {
                return false;
            }
            this.setted=true;
            this.result = result;
            resultHandlerToUse = this.resultHandler;
            if (resultHandlerToUse == null) {
                return true;
            }
            this.resultHandler = null;
        }
        resultHandlerToUse.handleResult(result, nettyHttpRequestData.getRequest(), nettyHttpRequestData.getContext());
        //处理完结果之后,调用完成的回调方法
        if(isCompleted()){
            return true;
        }
        synchronized (this){
            if(isCompleted()){
                return true;
            }
            completed=true;
        }
        if(this.completionCallback!=null){
            this.completionCallback.run();
        }
        return true;
    }

    /**
     * 触发超时,进行超时方法回调
     */
    public void triggerTimeout(){
        if (isExpired()||isSetted()) {
            return;
        }
        synchronized (this){
            if(isExpired()||isSetted()){
                return;
            }
            this.expired=true;
        }
        if(this.timeoutCallback!=null){
            this.timeoutCallback.run();
        }
    }
}
