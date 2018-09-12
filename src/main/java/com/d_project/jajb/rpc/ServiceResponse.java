package com.d_project.jajb.rpc;

import com.d_project.jajb.JSONSerializable;

@JSONSerializable
public class ServiceResponse {

  @JSONSerializable
  private String status;
  @JSONSerializable
  private String message;
  @JSONSerializable
  private Object result;

  public String getStatus() {
    return status;
  }
  public void setStatus(String status) {
    this.status = status;
  }
  public String getMessage() {
    return message;
  }
  public void setMessage(String message) {
    this.message = message;
  }
  public Object getResult() {
    return result;
  }
  public void setResult(Object result) {
    this.result = result;
  }
}
