package com.d_project.jajb;

import java.io.StringReader;
import java.io.StringWriter;

/**
 * JSON
 * @author Kazuhiko Arase
 */
public class JSON {

  private JSON() {
  }

  public static String stringify(final Object obj) throws Exception {
    final StringWriter sout = new StringWriter();
    final JSONWriter out = new JSONWriter(sout);
    try {
      out.writeAny(obj);
    } finally {
      out.close();
    }
    return sout.toString();
  }

  /**
   * parse JSON string into object.
   * @param str JSON string
   * @return
   * @throws Exception
   */
  public static Object parse(final String str) throws Exception {
    return parse(str, null);
  }

  /**
   * parse JSON string into specified class.
   * @param str JSON string
   * @param rootClass
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public static <T> T parse(
      final String str,
      final Class<T> rootClass) throws Exception {
    final DefaultJSONParserHandler handler =
        new DefaultJSONParserHandler(rootClass);
    final JSONParser in = new JSONParser(new StringReader(str), handler);
    try {
      in.parseAny();
      return (T)handler.getLastData();
    } finally {
      in.close();
    }
  }
}
