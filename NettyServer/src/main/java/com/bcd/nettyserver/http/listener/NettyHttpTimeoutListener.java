package com.bcd.nettyserver.http.listener;

import com.bcd.nettyserver.http.define.CommonConst;
import com.bcd.nettyserver.http.result.NettyHttpDeferredResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NettyHttpTimeoutListener implements Runnable{
    @Override
    public void run() {
        try {
            Long curTimeStamp = new Date().getTime();
            List<String> timeoutDeferredResultKeyList = new ArrayList<>();
            CommonConst.NETTY_DEFERRED_RESULT_MAP.forEach((k, v) -> {
                Long timeout = v.getTimeoutValue();
                Long startTimestamp = v.getStartTimestamp();
                if ((curTimeStamp - startTimestamp) >= timeout) {
                    timeoutDeferredResultKeyList.add(k);
                }
            });
            timeoutDeferredResultKeyList.forEach(e -> {
                NettyHttpDeferredResult nettyHttpDeferredResult = CommonConst.NETTY_DEFERRED_RESULT_MAP.remove(e);
                CommonConst.HTTP_REQUEST_HANDLE_THREAD_POOL.execute(() -> nettyHttpDeferredResult.triggerTimeout());
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
