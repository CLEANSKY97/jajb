package com.d_project.jajb.rpc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

/**
 * ResultHandler
 * @author Kazuhiko Arase
 */
public interface ResultHandler {

  void handle(HttpServletResponse response)
      throws ServletException, IOException;

}
