package com.zst.week9.q3.api;

import lombok.Data;

@Data
public class RpcfxResponse {
    private Object result;
    private boolean status;
    private Exception exception;
}
