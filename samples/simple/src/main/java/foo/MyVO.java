package foo;

import com.d_project.jajb.JSONField;
import com.d_project.jajb.JSONType;

@JSONType
public class MyVO {

  @JSONField
  private String message;
  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }

  // This property will not be serialized.
  private String notSerializable;
  public String getNotSerializable() { return notSerializable; }
  public void setNotSerializable(String notSerializable) {
    this.notSerializable = notSerializable;
  }
}
