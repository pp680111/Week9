package com.zst.week9.q3.demo.provider;

import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {
    @Autowired
    private  ResponseInterceptor responseInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(responseInterceptor);
    }

    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
        TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory();
        tomcatServletWebServerFactory.addConnectorCustomizers((connector)->{
            ProtocolHandler protocolHandler = connector.getProtocolHandler();
            if(protocolHandler instanceof Http11NioProtocol){
                Http11NioProtocol http11NioProtocol = (Http11NioProtocol)protocolHandler;
                http11NioProtocol.setKeepAliveTimeout(60000);//millisecond
                http11NioProtocol.setMaxKeepAliveRequests(Integer.MAX_VALUE);
            }
        });
        return tomcatServletWebServerFactory;
    }
}
