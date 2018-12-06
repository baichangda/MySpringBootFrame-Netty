package com.bcd.nettyserver.http.data;

import com.bcd.base.exception.BaseRuntimeException;
import com.bcd.nettyserver.http.anno.NettyHttpController;
import com.bcd.nettyserver.http.anno.NettyHttpRequestMapping;
import com.bcd.nettyserver.http.anno.NettyHttpRequestParam;
import com.bcd.nettyserver.http.define.NettyHttpRequestMethodEnum;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class NettyHttpRequestMethod {
    private String serverId;
    private String path;
    private NettyHttpRequestMethodEnum nettyHttpRequestMethodEnum;
    private LinkedHashMap<String, com.bcd.nettyserver.http.data.NettyHttpRequestParam> paramMap;
    private Object controllerObj;
    private Method method;
    private long time;
    private Class returnClass;
    private boolean needRequestData;

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public Class getReturnClass() {
        return returnClass;
    }

    public void setReturnClass(Class returnClass) {
        this.returnClass = returnClass;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public NettyHttpRequestMethodEnum getNettyHttpRequestMethodEnum() {
        return nettyHttpRequestMethodEnum;
    }

    public void setNettyHttpRequestMethodEnum(NettyHttpRequestMethodEnum nettyHttpRequestMethodEnum) {
        this.nettyHttpRequestMethodEnum = nettyHttpRequestMethodEnum;
    }

    public LinkedHashMap<String, com.bcd.nettyserver.http.data.NettyHttpRequestParam> getParamMap() {
        return paramMap;
    }

    public void setParamMap(LinkedHashMap<String, com.bcd.nettyserver.http.data.NettyHttpRequestParam> paramMap) {
        this.paramMap = paramMap;
    }

    public Object getControllerObj() {
        return controllerObj;
    }

    public void setControllerObj(Object controllerObj) {
        this.controllerObj = controllerObj;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isNeedRequestData() {
        return needRequestData;
    }

    public void setNeedRequestData(boolean needRequestData) {
        this.needRequestData = needRequestData;
    }

    public NettyHttpRequestMethod(String serverId, String path, NettyHttpRequestMethodEnum nettyHttpRequestMethodEnum, LinkedHashMap<String, com.bcd.nettyserver.http.data.NettyHttpRequestParam> paramMap, Object controllerObj, Class returnClass, Method method, long time, boolean needRequestData) {
        this.serverId=serverId;
        this.path = path;
        this.nettyHttpRequestMethodEnum = nettyHttpRequestMethodEnum;
        this.paramMap = paramMap;
        this.controllerObj=controllerObj;
        this.returnClass=returnClass;
        this.method=method;
        this.time=time;
        this.needRequestData=needRequestData;
    }

    /**
     * 执行方法,返回的结果集必须是JsonMessage
     * @param args
     * @return
     */
    public Object executeMethod(Object ... args) throws Exception{
        try {
            return  method.invoke(controllerObj,args);
        } catch (Exception e){
            throw BaseRuntimeException.getException(e);
        }
    }

    /**
     * 生成一个NettyController的 NettyHttpRequestMethod 集合
     * @param controllerObj
     * @return
     */
    public static List<NettyHttpRequestMethod> generateByNettyController(Object controllerObj){
        Class clazz=controllerObj.getClass();
        NettyHttpController nettyHttpController = (NettyHttpController)clazz.getAnnotation(NettyHttpController.class);
        String[] pre={""};
        NettyHttpRequestMapping controllerRequestMapping= (NettyHttpRequestMapping)clazz.getAnnotation(NettyHttpRequestMapping.class);
        if(controllerRequestMapping!=null){
            String value=controllerRequestMapping.value();
            pre[0]=value;
        }
        List<Method> methodList= MethodUtils.getMethodsListWithAnnotation(clazz, NettyHttpRequestMapping.class);
        return methodList.stream().map(method->{
            NettyHttpRequestMapping methodRequestMapping= method.getAnnotation(NettyHttpRequestMapping.class);
            String value=methodRequestMapping.value();
            String path=pre[0]+value;
            NettyHttpRequestMethodEnum nettyHttpRequestMethodEnum =methodRequestMapping.method();
            LinkedHashMap<String, com.bcd.nettyserver.http.data.NettyHttpRequestParam> paramMap=new LinkedHashMap<>();
            boolean needRequestData=false;
            for (Parameter parameter : method.getParameters()) {
                NettyHttpRequestParam requestParam= parameter.getAnnotation(NettyHttpRequestParam.class);
                if(requestParam==null){
                    if(NettyHttpRequestData.class.isAssignableFrom(parameter.getType())){
                        needRequestData=true;
                        continue;
                    }else {
                        continue;
                    }
                }
                String name=requestParam.value();
                paramMap.put(name,new com.bcd.nettyserver.http.data.NettyHttpRequestParam(name,parameter.getType(),requestParam.required()));
            }
            return new NettyHttpRequestMethod(nettyHttpController.value(),path, nettyHttpRequestMethodEnum,paramMap,controllerObj,method.getReturnType(),method,methodRequestMapping.timeout(),needRequestData);
        }).collect(Collectors.toList());
    }
}
