package com.zst.week9.q3.api;

import lombok.Data;

@Data
public class RpcfxRequest {
  private String serviceClass;
  private String method;
  private Object[] params;
}
