package com.d_project.jajb;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * JSON
 * @author Kazuhiko Arase
 */
public class JSON {

  private JSON() {
  }

  public static String stringify(final Object obj) throws IOException {
    final StringWriter sout = new StringWriter();
    final JSONWriter out = new JSONWriter(sout);
    try {
      out.writeAny(obj);
    } finally {
      out.close();
    }
    return sout.toString();
  }

  public static Object parse(final String str) throws IOException {
    final DefaultJSONParserHandler handler = new DefaultJSONParserHandler();
    final JSONParser in = new JSONParser(new StringReader(str), handler);
    try {
      in.parseAny();
      return handler.getLastData();
    } finally {
      in.close();
    }
  }
}
