package com.zst.week9.q3.client;

import com.alibaba.fastjson.JSON;
import com.zst.week9.q3.api.RpcfxRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 基于Netty的远程调用HttpClient
 *
 * TODO:start和end方法处理并发调用时的情况
 */
public class NettyHttpRequestClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyHttpRequestClient.class);

    private Channel channel;
    private EventLoopGroup eventLoopGroup;
    private String url;
    private String host;
    private int port;
    private Semaphore semaphore;
    private AtomicReference<String> responseObj;

    public NettyHttpRequestClient(String url) throws MalformedURLException {
        this.url = url;

        URL u = new URL(url);
        this.host = u.getHost();
        // 如果url指定了端口那么getPort返回-1，直接根据协议取默认值
        if (u.getPort() == -1) {
            if ("https".equals(u.getProtocol()) && u.getPort() == -1) {
                this.port = 443;
            } else {
                this.port = 80;
            }
        } else {
            this.port = u.getPort();
        }
        this.semaphore = new Semaphore(0);
        this.responseObj = new AtomicReference<>();
    }

    public void start() {
        if (this.channel != null && this.eventLoopGroup != null) {
            throw new IllegalStateException("Repeat start");
        }

        try {
            this.eventLoopGroup = new NioEventLoopGroup(1);
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(this.eventLoopGroup)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .channel(NioSocketChannel.class)
                    .handler(new NettyHttpRequestClientChannelInitializer(this.responseObj, semaphore))
                    .remoteAddress(this.host, this.port);
            ChannelFuture future = bootstrap.connect().sync();
            this.channel = future.channel();
        } catch (Exception e) {
            throw new RuntimeException("Create netty http client failed", e);
        }
    }

    public void end() {
        if (this.channel == null || this.eventLoopGroup == null) {
            throw new IllegalStateException("Not started");
        }

        this.channel.close().awaitUninterruptibly();
        this.eventLoopGroup.shutdownGracefully();
        this.channel = null;
        this.eventLoopGroup = null;
    }

    public String request(RpcfxRequest req) {
        if (channel == null) {
            throw new IllegalStateException("Client is closed");
        }

        if (req == null) {
            throw new IllegalArgumentException("Request entity is null");
        }

        // 发送请求
        String reqJson = JSON.toJSONString(req);
        byte[] reqJsonBytes = reqJson.getBytes(StandardCharsets.UTF_8);
        FullHttpRequest nettyRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
                HttpMethod.POST, this.url);
        nettyRequest.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=utf-8");
        nettyRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, Integer.toString(reqJsonBytes.length));
        // 没想到这里的header居然要手动设置。。。。
        nettyRequest.headers().set(HttpHeaderNames.HOST, this.url);
        nettyRequest.content().clear();
        nettyRequest.content().writeBytes(reqJsonBytes);
        this.channel.writeAndFlush(nettyRequest);

        // 发送完请求之后阻塞自身，等待唤醒
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            // 被interrupt的话不作处理
        }

        // 返回请求结果
        if (StringUtils.isEmpty(responseObj.get())) {
            throw new IllegalStateException("Not received response");
        }
        return responseObj.get();
    }

    static class NettyHttpRequestClientChannelInitializer extends ChannelInitializer<NioSocketChannel> {
        private AtomicReference<String> responseRef;
        private Semaphore semaphore;

        public NettyHttpRequestClientChannelInitializer(AtomicReference<String> responseRef, Semaphore semaphore) {
            this.responseRef = responseRef;
            this.semaphore = semaphore;
        }

        @Override
        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
            nioSocketChannel.pipeline()
                    .addLast(new HttpClientCodec())
                    .addLast(new HttpObjectAggregator(1024 * 1024))
                    .addLast(new SyncHttpResponseHandler(this.responseRef, this.semaphore));
        }
    }

    /**
     * 异步接收远程服务方返回的数据
     *
     * 异步处理比较难搞，所以先设计为每次只允许有一个调用，完成了必做题之后有空再调整为通过CompletableFuture实现支持多个同时调用吧
     */
    static class SyncHttpResponseHandler extends ChannelInboundHandlerAdapter {
        private static final Logger logger = LoggerFactory.getLogger(SyncHttpResponseHandler.class);

        private AtomicReference<String> responseRef;
        private Semaphore semaphore;

        public SyncHttpResponseHandler(AtomicReference<String> responseRef, Semaphore semaphore) {
            this.responseRef = responseRef;
            this.semaphore = semaphore;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            FullHttpResponse response = (FullHttpResponse) msg;
            String responseValue = response.content().toString(StandardCharsets.UTF_8);

            // 在没有调用Client对象的request方法时接收到了HttpResponse的话输出错误日志，不做处理
            if (!semaphore.hasQueuedThreads()) {
                logger.error("Receive unrequest data, " + responseValue);
                return;
            }
            // 唤醒等待中的request线程
            responseRef.set(responseValue);
            semaphore.release();
        }
    }
}
