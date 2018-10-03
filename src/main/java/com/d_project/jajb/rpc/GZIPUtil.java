package com.d_project.jajb.rpc;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * GZIPUtil
 * @author Kazuhiko Arase
 */
public class GZIPUtil {

  private GZIPUtil() {
  }

  public static void output(
    final HttpServletRequest request,
    final HttpServletResponse response,
    final OutputHandler outputHandler
  ) throws IOException {

    final String acceptEncoding = request.getHeader("accept-encoding");

    if (acceptEncoding != null) {
      if (acceptEncoding.indexOf("gzip") != -1) {
        response.setHeader("Content-Encoding", "gzip");
        final OutputStream out = new GZIPOutputStream(
            new BufferedOutputStream(response.getOutputStream() ) );
        try {
          outputHandler.writeTo(out);
        } finally {
          out.close();
        }
        return;
      }
    }

    final OutputStream out = new BufferedOutputStream(
        response.getOutputStream() );
    try {
      outputHandler.writeTo(out);
    } finally {
      out.close();
    }
  }

  public interface OutputHandler {
    void writeTo(OutputStream out) throws IOException;
  }
}
