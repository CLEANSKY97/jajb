package com.d_project.jajb;

@JSONSerializable
public class TestVO4 extends TestVO {

  @JSONSerializable
  private boolean flg;
  @JSONSerializable
  private int[] arr;

  public boolean isFlg() {
    return flg;
  }
  public void setFlg(boolean flg) {
    this.flg = flg;
  }
  public int[] getArr() {
    return arr;
  }
  public void setArr(int[] arr) {
    this.arr = arr;
  }

}
