package com.zst.week9.q3.client;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.zst.week9.q3.api.Filter;
import com.zst.week9.q3.api.LoadBalancer;
import com.zst.week9.q3.api.Router;
import com.zst.week9.q3.api.RpcfxRequest;
import com.zst.week9.q3.api.RpcfxResponse;
import net.sf.cglib.proxy.Enhancer;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class Rpcfx {

    static {
        ParserConfig.getGlobalInstance().addAccept("io.kimmking");
    }

    public static <T, filters> T createFromRegistry(final Class<T> serviceClass, final String zkUrl, Router router, LoadBalancer loadBalance, Filter filter) {

        // 加filte之一

        // curator Provider list from zk
        List<String> invokers = new ArrayList<>();
        // 1. 简单：从zk拿到服务提供的列表
        // 2. 挑战：监听zk的临时节点，根据事件更新这个list（注意，需要做个全局map保持每个服务的提供者List）

        List<String> urls = router.route(invokers);

        String url = loadBalance.select(urls); // router, loadbalance

        return (T) create(serviceClass, url, filter);

    }

    public static <T> T create(final Class<T> serviceClass, final String url, Filter... filters) {
        // 0. 替换动态代理 -> 字节码生成
        // 创建serviceClass类的代理类，并拦截所有的方法调用走到CglibInvocationHandler中
        Enhancer e = new Enhancer();
        e.setSuperclass(serviceClass);
        e.setCallback(new CglibInvocationHandler(serviceClass, url, filters));
        return (T) e.create();
    }
}
