package com.d_project.jajb;

import org.junit.Assert;
import org.junit.Test;

public class JSONTest {

  protected void test(String src) throws Exception{
    test(src, src);
  }

  protected void test(String src, String expected) throws Exception {
    String actual = JSON.stringify(JSON.parse(src) );
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void test1() throws Exception {

    test("null");
    test(" null ", "null");
    test("true");
    test("false");
    test("NaN");

    test("[1,20,-3]");
    test(" [01, 020 ,-03] ","[1,20,-3]");
    test("\"\\u3000\"","\"\u3000\"");
    test("[0.5,-0.5,0.3]");
    test("+1E2", "1E+2");
    test("-1E2", "-1E+2");
    test("[\"1\",\"2\",\"3\"]");
    test("[\"\\b\\f\\n\\t\\r\"]");

    test("[\"\\n\\t\\r\",[\"1\",\"2\",\"3\"]]");
    test("[{},{},{}]");
    test("{\"a\":[],\"c\":[],\"b\":[]}");
  }

}
