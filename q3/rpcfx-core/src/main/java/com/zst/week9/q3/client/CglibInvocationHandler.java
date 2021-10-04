package com.zst.week9.q3.client;

import com.alibaba.fastjson.JSON;
import com.zst.week9.q3.api.Filter;
import com.zst.week9.q3.api.RpcfxRequest;
import com.zst.week9.q3.api.RpcfxResponse;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Cglib中拦截方法调用的拦截器
 */
public class CglibInvocationHandler implements MethodInterceptor {
    public static final MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

    private final Class<?> serviceClass;
    private final String url;
    private final Filter[] filters;

    public <T> CglibInvocationHandler(Class<T> serviceClass, String url, Filter... filters) {
        this.serviceClass = serviceClass;
        this.url = url;
        this.filters = filters;
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

    public Object invoke(Method method, Object[] params) {

        // 加filter地方之二
        // mock == true, new Student("hubao");

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


        // 加filter地方之三
        // Student.setTeacher("cuijing");

        // 这里判断response.status，处理异常
        // 考虑封装一个全局的RpcfxException

        return JSON.parse(response.getResult().toString());
    }

    private RpcfxResponse post(RpcfxRequest req, String url) throws IOException {
        String reqJson = JSON.toJSONString(req);
        System.out.println("req json: "+reqJson);

        // 1.可以复用client
        // 2.尝试使用httpclient或者netty client
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSONTYPE, reqJson))
                .build();
        String respJson = client.newCall(request).execute().body().string();
        System.out.println("resp json: "+respJson);
        return JSON.parseObject(respJson, RpcfxResponse.class);
    }
}
