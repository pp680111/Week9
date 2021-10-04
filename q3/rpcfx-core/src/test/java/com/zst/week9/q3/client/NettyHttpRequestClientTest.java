package com.zst.week9.q3.client;

import com.zst.week9.q3.api.RpcfxRequest;
import org.junit.Test;

import java.net.MalformedURLException;

public class NettyHttpRequestClientTest {
    @Test
    public void testInvokeRemote() throws MalformedURLException {
        NettyHttpRequestClient client = new NettyHttpRequestClient("http://localhost:8080");
        client.start();
        RpcfxRequest request = new RpcfxRequest();
        request.setServiceClass("com.zst.week9.q3.demo.api.UserService");
        request.setMethod("findById");
        request.setParams(new Object[] {1});
        String response = client.request(request);
        System.out.println(response);
    }
}
