package com.d_project.jajb.rpc;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.d_project.jajb.JSONParser;

/**
 * RPCServlet
 * @author Kazuhiko Arase
 */
@SuppressWarnings("serial")
public class RPCServlet extends HttpServlet {

  @Override
  protected void doPost(
    final HttpServletRequest request,
    final HttpServletResponse response
  ) throws ServletException, IOException {

    final RPCInvokerHandler invoker = createInvoker();

    request.setCharacterEncoding("UTF-8");

    Map<String,Object> responseData = new LinkedHashMap<String, Object>();
    responseData.put("status", "success");

    final JSONParser parser = new JSONParser(request.getReader(), invoker);
    try {
      parser.parseAny();
      responseData.put("result", invoker.invoke() );
    } catch(Exception e) {
      responseData.put("status", "fail");
      responseData.put("message", e.getMessage() );
      e.printStackTrace();

    } finally {
      parser.close();
    }
    System.out.println(responseData);
  }

  protected RPCInvokerHandler createInvoker() {
    return new RPCInvokerHandler();
  }
}
