package com.d_project.jajb.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Base64
 * @author Kazuhiko Arase
 */
public class Base64 {

  private Base64() {
  }

  public static byte[] encode(final byte[] data) throws IOException {

    final ByteArrayOutputStream bout = new ByteArrayOutputStream();

    try {

      final Base64EncodeOutputStream out = new Base64EncodeOutputStream(bout);

      try {
        out.write(data);
      } finally {
        out.close();
      }

    } finally {
      bout.close();
    }

    return bout.toByteArray();
  }

  public static byte[] decode(final byte[] data) throws IOException {

    final ByteArrayOutputStream bout = new ByteArrayOutputStream();

    try {

      final Base64DecodeInputStream in = new Base64DecodeInputStream(new ByteArrayInputStream(data) );

      try {

        int b;
        while ( (b = in.read() ) != -1) {
          bout.write(b);
        }

      } finally {
        in.close(); 
      }

    } finally {
      bout.close();
    }

    return bout.toByteArray();
  }

  public static String toDataUrl(
    final String contentType,
    final String path
  ) throws IOException {
    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    final OutputStream out = new Base64EncodeOutputStream(bout);
    try {
      final InputStream in = new BufferedInputStream(
          new FileInputStream(new File(path) ) );
      try {
        final byte[] buf = new byte[4096];
        int len;
        while ( (len = in.read(buf) ) != -1) {
          out.write(buf, 0, len);
        }
      } finally {
        in.close();
      }
    } finally {
      out.close();
    }
    return "data:" + contentType + ";base64," +
      new String(bout.toByteArray(), "ISO-8859-1");
  }
}
