package com.d_project.jajb.rpc;

import com.d_project.jajb.JSONSerializable;
import com.d_project.jajb.TestVO;

@JSONSerializable
public class ResVO {
  @JSONSerializable
  private String status;
  @JSONSerializable
  private TestVO result;
  public String getStatus() {
    return status;
  }
  public void setStatus(String status) {
    this.status = status;
  }
  public TestVO getResult() {
    return result;
  }
  public void setResult(TestVO result) {
    this.result = result;
  }
}
