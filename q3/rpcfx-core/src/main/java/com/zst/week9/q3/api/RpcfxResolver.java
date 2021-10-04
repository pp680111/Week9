package com.zst.week9.q3.api;

public interface RpcfxResolver {
    <T> T resolve(String serviceClass) throws ClassNotFoundException;
}
