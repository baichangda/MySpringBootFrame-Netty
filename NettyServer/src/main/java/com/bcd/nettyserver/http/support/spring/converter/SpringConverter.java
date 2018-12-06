package com.bcd.nettyserver.http.support.spring.converter;

import com.bcd.nettyserver.http.convert.NettyHttpParamConverter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

/**
 * 使用spring的http参数转换器
 */
public class SpringConverter implements NettyHttpParamConverter{
    private ConversionService conversionService;

    public SpringConverter() {
        this.conversionService = new DefaultConversionService();
    }

    @Override
    public <T> T convert(Object source, Class<T> targetType) {
        return conversionService.convert(source,targetType);
    }
}
