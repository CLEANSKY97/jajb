package com.d_project.jajb;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * JSONWriter
 * @author Kazuhiko Arase
 */
public class JSONWriter extends FilterWriter {

  public JSONWriter(Writer out) {
    super(out);
  }

  @SuppressWarnings("unchecked")
  protected void writeObject(final Object obj) throws IOException {
    if (obj instanceof Map) {
      writeMap( (Map<String,Object>)obj);
    } else if (obj.getClass().getAnnotation(JSONSerializable.class) != null) {
      writeSerializable(obj);
    } else if (obj instanceof Iterable<?>) {
      writeIterable( (Iterable<Object>)obj);
    } else if (obj.getClass().isArray() ) {
      writeArray(obj);
    } else {
      throw new RuntimeException(
          "unexpected type:" + obj.getClass().getName() );
    }
  }

  protected void writeIterable(
      final Iterable<Object> items) throws IOException {

    out.write('[');

    int cnt = 0;

    for (Object item : items) {

      if (cnt > 0) {
        out.write(',');
      }

      writeAny(item);

      cnt += 1;
    }

    out.write(']');
  }

  protected void writeArray(final Object array) throws IOException {

    out.write('[');

    final int len = Array.getLength(array);
    for (int i = 0; i < len; i += 1) {

      if (i > 0) {
        out.write(',');
      }

      writeAny(Array.get(array, i) );
    }

    out.write(']');
  }

  protected void writeMap(final Map<String, Object> map) throws IOException {

    out.write('{');

    int cnt = 0;

    for (Entry<String, Object> entry : map.entrySet() ) {

      if (cnt > 0) {
        out.write(',');
      }

      try {

        writeCharSequence(entry.getKey() );
        out.write(':');
        writeAny(entry.getValue() );

      } catch(IOException e) {
        throw new RuntimeException(entry.getKey() + '.' + e.getMessage(), e);
      }

      cnt += 1;
    }

    out.write('}');
  }

  protected void writeSerializable(Object obj) throws IOException {

    out.write('{');

    int cnt = 0;

    final Metadata meta = MetadataCache.getMetadata(obj.getClass() );
    for (final FieldInfo fieldInfo : meta.getFieldInfos() ) {

      if (cnt > 0) {
        out.write(',');
      }

      try {

        writeCharSequence(fieldInfo.getName() );
        out.write(':');
        writeAny(fieldInfo.get(obj) );

      } catch(IOException e) {
        throw new RuntimeException(fieldInfo.getName() +
            ": " + e.getMessage(), e);
      }

      cnt += 1;
    }

    out.write('}');
  }

  protected void writeCharSequence(final CharSequence s) throws IOException {

    out.write('"');

    for (int i = 0; i < s.length(); i += 1) {

      char c = s.charAt(i);
      Character esc;
      
      if (c == '"' || c == '\\') {
        out.write('\\');
        out.write(c);
      } else if ( (esc = escapes.get(c) ) != null) {
        out.write('\\');
        out.write(esc.charValue() );
      } else if (0x20 <= c && c <= 0x7e &&
          c != '<' && c != '>' || 0x80 <= c) {
        out.write(c);
      } else {
        out.write("\\u");
        out.write(hex(c >>> 12) );
        out.write(hex(c >>> 8) );
        out.write(hex(c >>> 4) );
        out.write(hex(c >>> 0) );
      }
    }

    out.write('"');
  }

  public void writeAny(Object obj) throws IOException {

    if (obj == null) {
      out.write("null");
    } else if (obj.equals(Boolean.TRUE) ) {
      out.write("true");
    } else if (obj.equals(Boolean.FALSE) ) {
      out.write("false");
    } else if (obj instanceof CharSequence) {
      writeCharSequence( (CharSequence)obj);
    } else if (obj instanceof Number) {
      double n = ( (Number)obj).doubleValue();
      if (Double.isNaN(n) ) {
        out.write("NaN");
      } else {
        out.write(obj.toString() );
      }
    } else {
      writeObject(obj);
    }
  }

  private static final String HEX = "0123456789abcdef";

  private static char hex(int c) {
    return HEX.charAt(c & 0xf);
  }

  private static final Map<Character, Character> escapes;

  static {
    escapes = new HashMap<Character, Character>();
    escapes.put('\b', 'b');
    escapes.put('\f', 'f');
    escapes.put('\n', 'n');
    escapes.put('\r', 'r');
    escapes.put('\t', 't');
  }
}
