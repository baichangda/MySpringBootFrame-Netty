package com.bcd.nettyserver.http.handler;

import com.bcd.base.exception.BaseRuntimeException;
import com.bcd.base.message.JsonMessage;
import com.bcd.base.util.ExceptionUtil;
import com.bcd.nettyserver.http.convert.NettyHttpParamConverter;
import com.bcd.nettyserver.http.data.NettyHttpRequestMethod;
import com.bcd.nettyserver.http.data.NettyHttpRequestParam;
import com.bcd.nettyserver.http.data.NettyHttpRequestData;
import com.bcd.nettyserver.http.define.CommonConst;
import com.bcd.nettyserver.http.define.ErrorDefine;
import com.bcd.nettyserver.http.define.NettyHttpRequestMethodEnum;
import com.bcd.nettyserver.http.result.NettyHttpDeferredResult;
import com.bcd.nettyserver.http.util.NettyHttpUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 此handler必须紧跟 HttpRequestDecoder 或者 HttpServerCodec 后面
 * <p>
 * 负责将请求解析出来,与NettyController注解的类的方法对应上,并调用对应的方法
 */
@SuppressWarnings("unchecked")
public class NettyHttpRequestHandler extends ChannelInboundHandlerAdapter {
    public NettyHttpRequestHandler(String serverId, NettyHttpParamConverter converter) {
        this.uri_to_http_method_map=CommonConst.SERVER_ID_TO_REQUEST_METHOD_MAP_MAP.get(serverId);
        this.converter = converter;
    }

    /**
     * 服务器标识
     */
    private Map<String,NettyHttpRequestMethod> uri_to_http_method_map;

    /**
     * http请求参数转换器
     */
    private NettyHttpParamConverter converter;



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //过滤非FullHttpRequest请求
        if (!(msg instanceof FullHttpRequest)) {
            return;
        }

        //启动线程池处理任务
        CommonConst.HTTP_REQUEST_HANDLE_THREAD_POOL.execute(()-> {
            try {
                handle(ctx, msg);
            }catch (Exception e){
                handleException(ctx,e);
            }
        });
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        handleException(ctx,cause);
    }

    /**
     * netty发生异常时候处理办法
     * @param ctx
     * @param cause
     */
    private void handleException(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        JsonMessage res = JsonMessage.fail(cause.getMessage(), null, ExceptionUtil.getStackTraceMessage(cause));
        NettyHttpUtil.response(res, ctx, false);
    }

    /**
     * 解析请求参数
     * @return 包含所有请求参数的键值对, 如果没有参数, 则返回空Map
     *
     */
    private Map<String, String> parseParam(FullHttpRequest httpRequest){
        Map<String, String> paramMap = new HashMap<>();
        try {
            HttpMethod method = httpRequest.method();
            if (HttpMethod.GET == method) {
                // 是GET请求
                QueryStringDecoder decoder = new QueryStringDecoder(httpRequest.uri());
                decoder.parameters().entrySet().forEach(entry -> {
                    // entry.getValue()是一个List, 只取第一个元素
                    paramMap.put(entry.getKey(), entry.getValue().get(0));
                });
            } else if (HttpMethod.POST == method) {
                // 是POST请求
                HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(httpRequest);
                List<InterfaceHttpData> paramList = decoder.getBodyHttpDatas();
                for (InterfaceHttpData param : paramList) {
                    Attribute data = (Attribute) param;
                    paramMap.put(data.getName(), data.getValue());
                }
            }
        }catch (Exception e){
            throw BaseRuntimeException.getException(e);
        }
        return paramMap;
    }

    /**
     * 1、解析路径和参数
     * 2、匹配NettyController对应的url
     * 3、匹配参数,进行参数验证
     * 4、识别是否是延迟结果集方法,进行方法调用
     * @param ctx
     * @param msg
     * @throws Exception
     */
    private void handle(ChannelHandlerContext ctx, Object msg) throws Exception{
        FullHttpRequest request = (FullHttpRequest) msg;
        //进行路径和参数解析
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        String path = queryStringDecoder.path();
        //获取对应的请求方法
        NettyHttpRequestMethod nettyHttpRequestMethod = uri_to_http_method_map.get(path);
        if (nettyHttpRequestMethod == null) {
            //如果配置不存在,则返回异常
            throw ErrorDefine.REQUEST_PATH_NOT_EXIST.toRuntimeException(path);
        }

        //如果请求方式不匹配,返回异常
        HttpMethod httpMethod = request.method();
        NettyHttpRequestMethodEnum nettyHttpRequestMethodEnum = CommonConst.NETTY_METHOD_TO_MY_NETTY_METHOD.get(httpMethod);
        NettyHttpRequestMethodEnum defineNettyHttpRequestMethodEnum = nettyHttpRequestMethod.getNettyHttpRequestMethodEnum();
        if (nettyHttpRequestMethodEnum != defineNettyHttpRequestMethodEnum) {
            throw ErrorDefine.REQUEST_PATH_METHOD_NOT_SUPPORT.toRuntimeException(path, defineNettyHttpRequestMethodEnum.name().toString(), nettyHttpRequestMethodEnum.name().toString());
        }


        Map<String, String> newParamListMap = parseParam(request);
        //验证参数必填
        LinkedHashMap<String, NettyHttpRequestParam> defineParamMap = nettyHttpRequestMethod.getParamMap();
        Set<String> paramSet = newParamListMap.keySet().stream().filter(e -> e != null).collect(Collectors.toSet());
        List<NettyHttpRequestParam> missParamList = defineParamMap.values().stream().filter(e -> e.getRequired() && !paramSet.contains(e.getName())).collect(Collectors.toList());
        if (missParamList.size() > 0) {
            String errorMsg = missParamList.stream().map(e -> e.getName()).reduce("", (e1, e2) -> {
                StringBuffer sb = new StringBuffer();
                sb.append(e1);
                if (!"".equals(e1)) {
                    sb.append(",");
                }
                sb.append("[");
                sb.append(e2);
                sb.append("]");
                return sb.toString();
            });
            throw ErrorDefine.PARAM_REQUIRED.toRuntimeException(errorMsg.toString());
        }


        //如果配置存在,则调用对应的方法,获取返回结果
        NettyHttpRequestData nettyHttpRequestData = new NettyHttpRequestData(UUID.randomUUID().toString(), request, ctx);
        List<Object> valueList = (List<Object>) defineParamMap.keySet().stream().map(k -> {
            NettyHttpRequestParam param = defineParamMap.get(k);
            Object val = newParamListMap.get(k);
            if (val == null) {
                return null;
            }
            return converter.convert(val, param.getClazz());
        }).collect(Collectors.toList());
        //如果方法有NettyRequest参数,则传过去;否则接收返回结果(注意,参数必须位于最后一个)
        if (nettyHttpRequestMethod.isNeedRequestData()) {
            valueList.add(nettyHttpRequestData);
        }

        //根据请求方法的返回结果集,进行不同的任务启动
        Class returnClass = nettyHttpRequestMethod.getReturnClass();
        //只有返回结果集为延迟结果集时候才启动延迟机制;其他结果集直接执行任务
        if (returnClass != null && NettyHttpDeferredResult.class.isAssignableFrom(returnClass)) {
            nettyHttpRequestMethod.executeMethod(valueList.toArray());
        } else {
            JsonMessage res=(JsonMessage) nettyHttpRequestMethod.executeMethod(valueList.toArray());
            NettyHttpUtil.response(res, ctx, request.protocolVersion().isKeepAliveDefault());
        }
    }
}
