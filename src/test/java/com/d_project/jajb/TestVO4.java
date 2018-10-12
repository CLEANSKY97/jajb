package com.d_project.jajb;

@JSONType
public class TestVO4 extends TestVO {

  @JSONField
  private boolean flg;
  @JSONField
  private int[] arr;

  private String str = "$STR$";
  private String str2 = "$STR2$";

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
  public String getStr() {
    return str;
  }
  public void setStr(String str) {
    this.str = str;
  }
  public String getStr2() {
    return str2;
  }
  public void setStr2(String str2) {
    this.str2 = str2;
  }
}
