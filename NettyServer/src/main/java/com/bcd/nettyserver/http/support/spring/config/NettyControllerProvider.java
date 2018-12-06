package com.bcd.nettyserver.http.support.spring.config;

import com.bcd.nettyserver.http.anno.NettyHttpController;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * 定义NettyController注解类的扫描器
 */
public class NettyControllerProvider extends ClassPathScanningCandidateComponentProvider {
    public NettyControllerProvider(){
        super(false);
        addIncludeFilter(new AnnotationTypeFilter(NettyHttpController.class));
    }
}
