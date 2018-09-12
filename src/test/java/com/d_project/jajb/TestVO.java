package com.d_project.jajb;

import java.util.List;

@JSONSerializable
public class TestVO {

  @JSONSerializable
  private String str;
  @JSONSerializable
  private int num;
  @JSONSerializable
  private TestVO2 group;
  @JSONSerializable
  private List<TestVO3> items;

  public String getStr() {
    return str;
  }
  public void setStr(String str) {
    this.str = str;
  }
  public int getNum() {
    return num;
  }
  public void setNum(int num) {
    this.num = num;
  }
  public TestVO2 getGroup() {
    return group;
  }
  public void setGroup(TestVO2 group) {
    this.group = group;
  }
  public List<TestVO3> getItems() {
    return items;
  }
  public void setItems(List<TestVO3> items) {
    this.items = items;
  }

}
