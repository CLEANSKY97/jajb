package com.d_project.jajb;

@JSONSerializable
public class TestVO4 extends TestVO {

  @JSONSerializable
  private boolean flg;

  public boolean isFlg() {
    return flg;
  }
  public void setFlg(boolean flg) {
    this.flg = flg;
  }

}
