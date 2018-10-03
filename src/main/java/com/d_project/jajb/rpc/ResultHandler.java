package com.d_project.jajb.rpc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ResultHandler
 * @author Kazuhiko Arase
 */
public interface ResultHandler {

  /**
   * Handle a result and write output to the response.
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  void handle(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws ServletException, IOException;
}
