package com.d_project.jajb;

@JSONSerializable
public class TestVO2 {

  @JSONSerializable
  private String s1;
  @JSONSerializable
  private String s2;

  public String getS1() {
    return s1;
  }
  public void setS1(String s1) {
    this.s1 = s1;
  }
  public String getS2() {
    return s2;
  }
  public void setS2(String s2) {
    this.s2 = s2;
  }

}
