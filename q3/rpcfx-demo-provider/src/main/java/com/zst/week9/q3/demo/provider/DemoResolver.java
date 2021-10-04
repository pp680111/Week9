package com.zst.week9.q3.demo.provider;

import com.zst.week9.q3.api.RpcfxResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class DemoResolver implements RpcfxResolver, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(DemoResolver.class);
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <T> T resolve(String serviceClass) throws ClassNotFoundException {
        /*
           先尝试通过sercice名称加载Class，加载不到抛异常直接返回null
           原先通过把serviceClass作为beanName来查找bean存在一定的缺陷，需要手动的为所有的提供服务的bean设置类路径作为beanName，
           因此还是使用Class来查找是比较合适的
        */
        Class<T> clazz = (Class<T>) this.getClass().getClassLoader().loadClass(serviceClass);
        return applicationContext.getBean(clazz);
    }
}
