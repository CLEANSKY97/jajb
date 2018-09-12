package com.d_project.jajb.rpc;

import com.d_project.jajb.JSONSerializable;

@JSONSerializable
public class ServiceRequest {

  @JSONSerializable
  private String serviceName;
  @JSONSerializable
  private String methodName;
  @JSONSerializable
  private Object[] arguments;

  public String getServiceName() {
    return serviceName;
  }
  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }
  public String getMethodName() {
    return methodName;
  }
  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }
  public Object[] getArguments() {
    return arguments;
  }
  public void setArguments(Object[] arguments) {
    this.arguments = arguments;
  }
}