package com.bcd.nettyserver.http.support.spring.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.*;

import java.util.Set;

/**
 * 进行com.bcd包下面NettyController注解类的扫描,并将其加入到spring ioc中
 */
@Configuration
public class NettyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();
    private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        registerNettyControllerBeans(registry);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    private void registerNettyControllerBeans(BeanDefinitionRegistry registry){
        NettyControllerProvider nettyControllerProvider=new NettyControllerProvider();
        Set<BeanDefinition> beanDefinitionSet= nettyControllerProvider.findCandidateComponents("com.bcd");

        beanDefinitionSet.stream().forEach(e->{
            ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(e);
            e.setScope(scopeMetadata.getScopeName());
            // 可以自动生成name
            String beanName = this.beanNameGenerator.generateBeanName(e, registry);

            BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(e, beanName);
            BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
        });
    }
}
