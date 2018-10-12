package com.d_project.jajb.helper;

import java.io.PrintWriter;

public class PerfTestGenBean {
  public static void main(String[] args) throws Exception {
    PrintWriter out = new PrintWriter(
        "src/test/java/com/d_project/jajb/PerfTestVO.java", "UTF-8");
    try {
      out.println("package com.d_project.jajb;");
      out.println("import java.util.List;");
      out.println();
      out.println("@JSONType");
      out.println("public class PerfTestVO {");
      for (int i = 0; i < 100; i += 1) {

        String type;
        if (i == 0) {
          type = "List<PerfTestVO>";
        } else if (i % 10 == 0) {
          type = "int";
        } else if (i % 10 == 1) {
          type = "boolean";
        } else {
          type = "java.lang.String";
        }

        out.println("  @JSONField(order="  + (i + 1) + ")");
        out.println("  private " + type + " field" + i + ";");
        out.println("  public " + type + " getField" + i + "() { return field" + i + "; }");
        out.println("  public void setField" + i + "(" + type + " field" + i + ") { this.field" + i + " = field" + i + "; }");
      }
      out.println("}");

    } finally {
      out.close();
    }
  }
}