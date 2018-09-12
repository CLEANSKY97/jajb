package com.d_project.jajb;

import java.io.EOFException;
import java.io.FilterReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * JSONParser
 * @author Kazuhiko Arase
 */
public class JSONParser extends FilterReader {

  private int buf;
  private boolean eof;
  private final JSONParserHandler handler;

  public JSONParser(final Reader in,
      final JSONParserHandler handler) {
    super(in);
    this.buf = -1;
    this.eof = false;
    this.handler = handler;
  }

  protected void inc() throws Exception {
    if (buf == -1) {
      throw new Exception();
    }
    buf = -1;
  }

  protected char seekChar() throws Exception {
    return seekChar(false);
  }

  protected char seekChar(final boolean strict) throws Exception {
    if (eof) {
      throw new EOFException();
    }
    while (!eof) {
      if (buf == -1) {
        buf = in.read();
        if (buf == -1) {
          eof = true;
        }
      }
      if (buf != -1) {
        char c = (char)buf;
        if (strict || "\u0020\t\r\n".indexOf(c) == -1) {
          return c;
        }
        // skip whitespaces
        inc();
      }
    }
    return (char)0;
  }

  protected void parseObject() throws Exception {

    handler.beginObject();

    char c;

    c = seekChar();
    inc();

    while (true) {

      c = seekChar();
      if (c == '}') {
        inc();
        break;
      } else if (c == ',') {
        inc();
      }

      c = seekChar();
      if (c != '"') {
        throw new Exception("invalid char:" + c);
      }

      handler.onData(readString() );

      c = seekChar();
      if (c != ':') {
        throw new Exception("invalid char:" + c);
      }

      inc();

      parseAny();
    }

    handler.endObject();
  }

  protected void parseArray() throws Exception {

    handler.beginArray();

    char c;

    c = seekChar();
    inc();

    while (true) {

      c = seekChar();
      if (c == ']') {
        inc();
        break;
      } else if (c == ',') {
        inc();
      }
      parseAny();
    }

    handler.endArray();
  }

  protected Object readKeyword(
      final String keyword, final Object object) throws Exception {
    for (int i = 0; i < keyword.length(); i += 1) {
      final char c = seekChar(true);
      if (c != keyword.charAt(i) ) {
        throw new Exception("invalid char:" + c);
      }
      inc();
    }
    return object;
  }

  protected String readString() throws Exception {

    final StringBuilder str = new StringBuilder();
    char c;

    c = seekChar();
    inc();

    while (true) {

      c = seekChar(true);
      if (c == '"') {
        inc();
        return str.toString();
      } else if (c == '\\') {
        // escapes
        inc();
        c = seekChar(true);
        Character escChar = escapes.get(c);
        if (escChar != null) {
          str.append(escChar.charValue() );
          inc();
        } else if (c == 'u') {
          inc();
          int charCode = 0;
          for (int i = 0; i < 4; i += 1) {
            c = seekChar(true);
            if (!isHexChar(c) ) {
              throw new Exception("invalid char:" + c);
            }
            inc();
            charCode = (charCode << 4) | 
              Integer.parseInt(String.valueOf(c), 16);
          }
          str.append( (char)charCode);
        } else {
          str.append(c);
          inc();
        }
      } else {
        str.append(c);
        inc();
      }
    }
  }

  protected void readNumChars(final StringBuilder num) throws Exception {
    while (true) {
      final char c = seekChar(true);
      if (!isNumChar(c) ) {
        break;
      }
      num.append(c);
      inc();
    }
  }

  protected BigDecimal readNumber() throws Exception {

    final StringBuilder num = new StringBuilder();
    char c;

    // ipart
    c = seekChar();
    if (isSignChar(c) ) {
      num.append(c);
      inc();
    }
    readNumChars(num);

    // fpart
    c = seekChar();
    if (c == '.') {
      num.append(c);
      inc();
      readNumChars(num);
    }

    // exp
    c = seekChar();
    if (c == 'e' || c == 'E') {
      num.append(c);
      inc();
      c = seekChar();
      if (isSignChar(c) ) {
        num.append(c);
        inc();
      }
      readNumChars(num);
    }

    return new BigDecimal(num.toString() );
  }

  public void parseAny() throws Exception {
    final char c = seekChar();
    if (c == 0) {
      handler.onData(null);
    } else if (c == 'n') {
      handler.onData(readKeyword("null", null) );
    } else if (c == 't') {
      handler.onData(readKeyword("true", Boolean.TRUE) );
    } else if (c == 'f') {
      handler.onData(readKeyword("false", Boolean.FALSE) );
    } else if (c == '"') {
      handler.onData(readString() );
    } else if (c == 'N') {
      handler.onData(readKeyword("NaN", Double.NaN) );
    } else if (isSignChar(c) || isNumChar(c) ) {
      handler.onData(readNumber() );
    } else if (c == '[') {
      parseArray();
    } else if (c == '{') {
      parseObject();
    } else {
      throw new Exception("invalid char:" + c);
    }
  }

  private static final Map<Character, Character> escapes;

  static {
    escapes = new HashMap<Character, Character>();
    escapes.put('b', '\b');
    escapes.put('f', '\f');
    escapes.put('n', '\n');
    escapes.put('r', '\r');
    escapes.put('t', '\t');
  }

  public static boolean isSignChar(final char c) {
    return c == '+' || c == '-';
  }

  public static boolean isNumChar(final char c) {
    return '0' <= c && c <= '9';
  }

  public static boolean isHexChar(final char c) {
    return '0' <= c && c <= '9' ||
      'a' <= c && c <= 'f' || 'A' <= c && c <= 'F';
  }
}
