package com.bcd.nettyserver.http.define;

import com.bcd.base.i18n.I18NData;
import com.bcd.base.message.ErrorMessage;

public class ErrorDefine {
    /**
     * 请求路径不存在
     */
    public final static ErrorMessage REQUEST_PATH_NOT_EXIST=ErrorMessage.getMessage(I18NData.getI18NData("NettyHttpRequestHandler.channelRead.requestPathNotExist"));
    /**
     * 请求路径请求方法不支持
     */
    public final static ErrorMessage REQUEST_PATH_METHOD_NOT_SUPPORT=ErrorMessage.getMessage(I18NData.getI18NData("NettyHttpRequestHandler.channelRead.requestPathMethodNotSupport"));
    /**
     * 参数必填
     */
    public final static ErrorMessage PARAM_REQUIRED=ErrorMessage.getMessage(I18NData.getI18NData("NettyHttpRequestHandler.channelRead.paramRequired"));
}
