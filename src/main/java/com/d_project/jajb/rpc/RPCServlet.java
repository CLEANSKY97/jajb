package com.d_project.jajb.rpc;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.d_project.jajb.JSONParser;
import com.d_project.jajb.JSONWriter;

/**
 * RPCServlet
 * @author Kazuhiko Arase
 */
@SuppressWarnings("serial")
public class RPCServlet extends HttpServlet {

  // TODO kw..

  private static final String STATUS_KEY = "status";
  private static final String STATUS_SUCCESS = "success";
  private static final String STATUS_FAILURE = "failule";

  private static final String RESULT_KEY = "result";
  private static final String MESSAGE_KEY = "message";

  @Override
  protected void doPost(
    final HttpServletRequest request,
    final HttpServletResponse response
  ) throws ServletException, IOException {

    final RPCInvokerHandler invoker = createInvoker();

    request.setCharacterEncoding("UTF-8");

    final Map<String,Object> responseData = new LinkedHashMap<String, Object>();

    final JSONParser parser = new JSONParser(request.getReader(), invoker);

    try {
      parser.parseAny();
      responseData.put(STATUS_KEY, STATUS_SUCCESS);
      responseData.put(RESULT_KEY, invoker.invoke() );
    } catch(Exception e) {
      responseData.put(STATUS_KEY, STATUS_FAILURE);
      responseData.put(MESSAGE_KEY, e.getMessage() );
      e.printStackTrace();
    } finally {
      parser.close();
    }

    response.setContentType("application/json");
    final JSONWriter out = new JSONWriter(response.getWriter() );
    try {
      out.writeAny(responseData);
    } finally {
      out.close();
    }
  }

  protected RPCInvokerHandler createInvoker() {
    return new RPCInvokerHandler();
  }
}
