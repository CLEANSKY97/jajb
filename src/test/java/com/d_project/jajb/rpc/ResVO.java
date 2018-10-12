package com.d_project.jajb.rpc;

import com.d_project.jajb.JSONField;
import com.d_project.jajb.JSONType;
import com.d_project.jajb.TestVO;

@JSONType
public class ResVO {
  @JSONField
  private String status;
  @JSONField
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
