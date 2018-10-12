package com.d_project.jajb;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerfTest {

  public static void main(String[] args) throws Exception {
    new PerfTest().start();
  }

  protected static final Logger logger =
      LoggerFactory.getLogger(PerfTest.class);

  private static final String strChars = "abcdefgh12345678";
  private Random random;

  public PerfTest() {
    random = new Random(0);
  }
  public void start() throws Exception {
    DecimalFormat df = new DecimalFormat("###,###,###,##0");
    int count = 0;
    while (true) {

      final long t1 = System.nanoTime();
      final Map<String, Object> obj = createObject(true);
      final String s1 = JSON.stringify(obj);
      final PerfTestVO vo = JSON.parse(JSON.stringify(obj), PerfTestVO.class);
      final String s2 = JSON.stringify(vo);

        if (!s1.equals(s2) ) {
          PrintWriter out = new PrintWriter("err.txt", "UTF-8");
          try {
            out.println(s1);
            out.println(s2);
          } finally {
            out.close();
          }
          throw new RuntimeException("not equals");
        }


      final long t2 = System.nanoTime();

      logger.info("#" + count +
          " - len:" + df.format(s2.length() ) + "[bytes]" +
          " - time:" + df.format(t2 - t1) + "[ns]");

      count += 1;

      Thread.sleep(500);
    }
  }

  protected String createString() {
    StringBuilder buf = new StringBuilder();
    int len = (int)(random.nextDouble() * 50);
    for (int i = 0; i < len; i += 1) {
      buf.append(strChars.charAt(
          (int)(random.nextDouble() * strChars.length() ) ));
    }
    return buf.toString();
  }

  protected Map<String, Object> createObject(boolean withList) {
    Map<String, Object> obj = new LinkedHashMap<String, Object>();
    for (int i = 0; i < 100; i += 1) {
      Object value;
      if (i == 0) {
        if (withList) {
          List<Map<String, Object>> list =  new ArrayList<Map<String, Object>>();
          for (int j = 0; j < 1000; j += 1) {
            list.add(createObject(false) );
          }
          value = list;
        } else {
          value = null;
        }
      } else if (i % 10 == 0) {
        value = random.nextInt();
      } else if (i % 10 == 1) {
        value = random.nextDouble() < 0.5;
      } else {
        value = createString();
      }
      obj.put("field" + i, value);
    }
    return obj;
  }
}
