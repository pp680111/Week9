package com.zst.week9.q3.client;

import com.alibaba.fastjson.JSON;
import com.zst.week9.q3.api.Filter;
import com.zst.week9.q3.api.RpcfxRequest;
import com.zst.week9.q3.api.RpcfxResponse;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import okhttp3.MediaType;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;

/**
 * Cglib中拦截方法调用的拦截器
 */
public class CglibInvocationHandler implements MethodInterceptor {
    public static final MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

    private final Class<?> serviceClass;
    private final String url;
    private final Filter[] filters;
    private NettyHttpRequestClient requestClient;

    public <T> CglibInvocationHandler(Class<T> serviceClass, String url, Filter... filters) {
        this.serviceClass = serviceClass;
        this.url = url;
        this.filters = filters;
        try {
            this.requestClient = new NettyHttpRequestClient(url);
            this.requestClient.start();
        } catch (MalformedURLException urle) {
            throw new IllegalArgumentException("url format error");
        }
    }

    /**
     * 拦截所有的方法调用，转为远程调用
     * @param obj 被调用对象
     * @param method 被调用方法
     * @param args 方法调用参数列表
     * @param proxy 方法代理信息
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return this.invoke(method, args);
    }

    /**
     * 负责处理远程方法调用
     * @param method
     * @param params
     * @return
     */
    public Object invoke(Method method, Object[] params) {
        RpcfxRequest request = new RpcfxRequest();
        request.setServiceClass(this.serviceClass.getName());
        request.setMethod(method.getName());
        request.setParams(params);

        if (null!=filters) {
            for (Filter filter : filters) {
                if (!filter.filter(request)) {
                    return null;
                }
            }
        }

        RpcfxResponse response = null;
        try {
            response = post(request, url);
            if (!response.isStatus()) {
                throw new Exception("服务提供方发生错误, 错误原因="
                        + response.getException() != null ? response.getException().getMessage(): "");
            }
        } catch (Exception e) {
            throw new RpcfxException(String.format("远程调用时发生错误, url=%s, 错误信息=%s", url, e.getMessage()));
        }

        return JSON.parse(response.getResult().toString());
    }

    private RpcfxResponse post(RpcfxRequest req, String url) throws IOException {
        String respJson = this.requestClient.request(req);
        System.out.println("resp json: "+respJson);
        return JSON.parseObject(respJson, RpcfxResponse.class);
    }
}
