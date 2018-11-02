package ws;

import com.d_project.jajb.JSONField;
import com.d_project.jajb.JSONType;

@JSONType
public class MyVO {
  @JSONField
  private String f1;
  @JSONField
  private int f2;
  public String getF1() {
    return f1;
  }
  public void setF1(String f1) {
    this.f1 = f1;
  }
  public int getF2() {
    return f2;
  }
  public void setF2(int f2) {
    this.f2 = f2;
  }
}
