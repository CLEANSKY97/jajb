package com.d_project.jajb;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

import com.d_project.jajb.helper.ObjectUtil;

/**
 * JSONTest
 * @author Kazuhiko Arase
 */
public class JSONTest {

  @Test
  public void testPlain() throws Exception {

    assertJSON2JSON("null");
    assertJSON2JSON(" null ", "null");
    assertJSON2JSON("true");
    assertJSON2JSON("false");
    assertJSON2JSON("NaN");
    assertJSON2JSON("+1E2", "1E+2");
    assertJSON2JSON("-1E2", "-1E+2");
    assertJSON2JSON("\"\"","\"\"");
    assertJSON2JSON("\"a\"","\"a\"");
    assertJSON2JSON(" \"a\"","\"a\"");
    assertJSON2JSON("\"\\u3000\"","\"\u3000\"");

  }

  @Test
  public void testNumber() throws Exception {
    assertNumber("-1.25");
    assertInt("-1");
    assertInt("0");
    assertInt("-0");
    assertInt("+0");
    assertInt("1");
    assertInt("+1");
    assertNumber("1.25");
    assertNumber("+1.25");
  }

  @Test
  
  public void testObjects() throws Exception {

    assertJSON2JSON("[1,20,-3]");
    assertJSON2JSON(" [01, 020 ,-03] ","[1,20,-3]");
    assertJSON2JSON("\"\\u3000\"","\"\u3000\"");
    assertJSON2JSON("[0.5,-0.5,0.3]");
    assertJSON2JSON("[\"1\",\"2\",\"3\"]");
    assertJSON2JSON("[\"\\b\\f\\n\\t\\r\"]");

  }

  @Test
  public void testComplex() throws Exception {
    assertJSON2JSON("[\"\\n\\t\\r\",[\"1\",\"2\",\"3\"]]");
    assertJSON2JSON("[{},{},{}]");
    assertJSON2JSON("[1,2,[3],4,[5,6],7]");
    assertJSON2JSON("{\"a\":[],\"c\":[],\"b\":[]}");
  }

  @Test
  public void testPOJO1() throws Exception {
    assertJSON2JSON("{\"group\":null,\"items\":null,\"num\":1,\"str\":\"1\"}",
        TestVO.class, new ObjectHandler() {
      @Override
      public Object handle(Object obj) {
        Assert.assertEquals(TestVO.class, obj.getClass() );
        TestVO vo = (TestVO)obj;
        Assert.assertEquals("1", vo.getStr() );
        Assert.assertEquals(1, vo.getNum() );
        Assert.assertNull(vo.getGroup() );
        Assert.assertNull(vo.getItems() );
        return obj;
      }
    });
  }

  @Test
  public void testPOJO2() throws Exception {
    assertJSON2JSON("{\"group\":{}," +
        "\"items\":[],\"num\":1,\"str\":\"1\"}",
        "{\"group\":{\"s1\":null,\"s2\":null}," +
        "\"items\":[],\"num\":1,\"str\":\"1\"}",
        TestVO.class, new ObjectHandler() {
      @Override
      public Object handle(Object obj) {
        Assert.assertEquals(TestVO.class, obj.getClass() );
        TestVO vo = (TestVO)obj;
        Assert.assertEquals("1", vo.getStr() );
        Assert.assertEquals(1, vo.getNum() );
        Assert.assertNotNull(vo.getGroup() );
        Assert.assertNotNull(vo.getItems() );
        return obj;
      }
    });
  }

  @Test
  public void testPOJO3() throws Exception {
    assertJSON2JSON("{\"group\":{\"s1\":\"a\",\"s2\":\"b\"}," +
        "\"items\":[{\"f1\":\"@\",\"f2\":2},{\"f1\":\"%\"}],\"num\":1,\"str\":\"1\"}",
        "{\"group\":{\"s1\":\"a\",\"s2\":\"b\"}," +
        "\"items\":[{\"f1\":\"@\"},{\"f1\":\"%\"}],\"num\":1,\"str\":\"1\"}",
        TestVO.class, new ObjectHandler() {
      @Override
      public Object handle(Object obj) {
        Assert.assertEquals(TestVO.class, obj.getClass() );
        TestVO vo = (TestVO)obj;
        Assert.assertEquals("1", vo.getStr() );
        Assert.assertEquals(1, vo.getNum() );
        Assert.assertNotNull(vo.getGroup() );
        Assert.assertEquals("a", vo.getGroup().getS1() );
        Assert.assertEquals("b", vo.getGroup().getS2() );
        Assert.assertNotNull(vo.getItems() );
        Assert.assertEquals(2, vo.getItems().size() );
        Assert.assertEquals("@", vo.getItems().get(0).getF1() );
        Assert.assertEquals("%", vo.getItems().get(1).getF1() );
        return obj;
      }
    });
  }

  @Test
  public void testPOJO4() throws Exception {
    assertJSON2JSON("{\"arr\":[1,2,3],\"flg\":true,\"group\":null," +
        "\"items\":null,\"num\":1,\"str\":\"1\",\"str2\":\"2\"}",
        "{\"arr\":[1,2,3],\"flg\":true,\"group\":null," +
            "\"items\":null,\"num\":1,\"str\":\"1\"}",
        TestVO4.class, new ObjectHandler() {
      @Override
      public Object handle(Object obj) {
        Assert.assertEquals(TestVO4.class, obj.getClass() );
        TestVO4 vo = (TestVO4)obj;
        Assert.assertEquals("1", vo.getStr() );
        Assert.assertEquals("$STR2$", vo.getStr2() );
        Assert.assertEquals(1, vo.getNum() );
        Assert.assertTrue(vo.isFlg() );
        Assert.assertNull(vo.getGroup() );
        Assert.assertNull(vo.getItems() );
        Assert.assertEquals(3, vo.getArr().length);
        Assert.assertEquals(1, vo.getArr()[0]);
        Assert.assertEquals(2, vo.getArr()[1]);
        Assert.assertEquals(3, vo.getArr()[2]);
        return obj;
      }
    });
  }

  @Test
  public void testPOJO5() throws Exception {
    Assert.assertEquals("[1,2]",JSON.stringify(new byte[] {1,2}) );
    Assert.assertEquals("[1,2]",JSON.stringify(new short[] {1,2}) );
    Assert.assertEquals("[1,2]",JSON.stringify(new int[] {1,2}) );
    Assert.assertEquals("[1,2]",JSON.stringify(new long[] {1,2}) );
    Assert.assertEquals("[1.0,2.0]",JSON.stringify(new float[] {1,2}) );
    Assert.assertEquals("[1.0,2.0]",JSON.stringify(new double[] {1,2}) );
  }

  @Test
  public void testPOJO6() throws Exception {
    Assert.assertArrayEquals(new byte[] {1,2},
        JSON.parse("[1,2]", byte[].class) );
    Assert.assertArrayEquals(new int[] {1,2},
        JSON.parse("[1,2]", int[].class) );
    Assert.assertArrayEquals(new int[] {1,2},
        JSON.parse("[1,2]", int[].class) );
    Assert.assertArrayEquals(new long[] {1,2},
        JSON.parse("[1,2]", long[].class) );
    Assert.assertArrayEquals(new float[] {1.0f,2},
        JSON.parse("[1,2]", float[].class), 0);
    Assert.assertArrayEquals(new double[] {1,2.0},
        JSON.parse("[1,2]", double[].class), 0);
    Assert.assertArrayEquals(new BigInteger[] {
        BigInteger.valueOf(1), BigInteger.valueOf(2)},
        JSON.parse("[1,2]", BigInteger[].class) );
    Assert.assertArrayEquals(new BigDecimal[] {
        BigDecimal.valueOf(1), BigDecimal.valueOf(2)},
        JSON.parse("[1,2]", BigDecimal[].class) );
  }

  @Test
  public void testPOJO7() throws Exception {

    Object in = ObjectUtil.asMap(
        "str", "a",
        "num", 123,
        "group", ObjectUtil.asMap("s1", "b", "s2", "c"),
        "items", ObjectUtil.asList(
            ObjectUtil.asMap("f1", "x"),
            ObjectUtil.asMap("f1", "y"),
            ObjectUtil.asMap("f1", null) ) );

    TestVO out = JSON.parse(JSON.stringify(in), TestVO.class);

    Assert.assertEquals("a", out.getStr() );
    Assert.assertEquals(123, out.getNum() );

    Assert.assertEquals("b", out.getGroup().getS1() );
    Assert.assertEquals("c", out.getGroup().getS2() );

    Assert.assertEquals(3, out.getItems().size() );
    Assert.assertEquals("x", out.getItems().get(0).getF1() );
    Assert.assertEquals("y", out.getItems().get(1).getF1() );
    Assert.assertNull(out.getItems().get(2).getF1() );
  }

  protected final Logger logger = Logger.getLogger(getClass().getName() );

  protected interface ObjectHandler {
    Object handle(Object obj);
  }

  protected void assertJSON2JSON(String src) throws Exception{
    assertJSON2JSON(src, src);
  }

  protected static void assertInt(String numStr) throws Exception {
    final BigDecimal bigDec = new BigDecimal(numStr);
    final BigInteger bigInt = new BigInteger(numStr);
    Assert.assertEquals(bigDec, JSON.parse(numStr, BigDecimal.class) );
    Assert.assertEquals(bigInt, JSON.parse(numStr, BigInteger.class) );
    Assert.assertEquals(bigDec.byteValue(), (byte)JSON.parse(numStr, Byte.TYPE) );
    Assert.assertEquals(bigDec.intValue(), (int)JSON.parse(numStr, Integer.TYPE) );
    Assert.assertEquals(bigDec.shortValue(), (int)JSON.parse(numStr, Short.TYPE) );
    Assert.assertEquals(bigDec.longValue(), (long)JSON.parse(numStr, Long.TYPE) );
    Assert.assertEquals(bigDec.floatValue(), (float)JSON.parse(numStr, Float.TYPE), 0);
    Assert.assertEquals(bigDec.doubleValue(), (double)JSON.parse(numStr, Double.TYPE), 0);
  }

  protected static void assertNumber(String numStr) throws Exception {
    final BigDecimal bigDec = new BigDecimal(numStr);
    Assert.assertEquals(bigDec, JSON.parse(numStr, BigDecimal.class) );
    Assert.assertEquals(bigDec.floatValue(), (float)JSON.parse(numStr, Float.TYPE), 0);
    Assert.assertEquals(bigDec.doubleValue(), (double)JSON.parse(numStr, Double.TYPE), 0);
  }

  protected static void assertJSON2JSON(String src, String expected) throws Exception {
    String actual = JSON.stringify(JSON.parse(src) );
    Assert.assertEquals(expected, actual);
  }

  protected static void assertJSON2JSON(String src,
      Class<?> targetClass,
      ObjectHandler h) throws Exception{
    assertJSON2JSON(src, src, targetClass, h);
  }

  protected static void assertJSON2JSON(String src, String expected,
      Class<?> targetClass,
      ObjectHandler h) throws Exception {
    String actual = JSON.stringify(h.handle(JSON.parse(src, targetClass) ) );
    Assert.assertEquals(expected, actual);
  }
}
