package com.d_project.jajb.rpc;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.d_project.jajb.JSONParser;
import com.d_project.jajb.JSONWriter;

/**
 * RPCServlet
 * @author Kazuhiko Arase
 */
@SuppressWarnings("serial")
public class RPCServlet extends HttpServlet {

  private static final String STATUS_KEY = "status";
  private static final String STATUS_SUCCESS = "success";
  private static final String STATUS_FAILURE = "failule";

  private static final String RESULT_KEY = "result";
  private static final String MESSAGE_KEY = "message";

  protected static final Logger logger =
      LoggerFactory.getLogger(RPCServlet.class);

  @Override
  public void init(final ServletConfig config) throws ServletException {
    super.init(config);

    try {
      loadServices(config);
    } catch(RuntimeException e) {
      throw e;
    } catch(Exception e) {
      throw new ServletException(e);
    }
  }

  protected void loadServices(
      final ServletConfig config) throws Exception {
    final String path = config.getInitParameter("services");
    logger.debug("loading services from " + path);
    final Properties services = new Properties();
    final InputStream in = config.getServletContext().
        getResourceAsStream(path);
    try {
      services.load(in);
    } finally {
      in.close();
    }
    for (final Entry<Object,Object> entry : services.entrySet() ) {
      final String serviceName = (String)entry.getKey();
      final String serviceClassName = (String)entry.getValue();
      logger.debug(serviceName + " = " + serviceClassName);
      ServiceLocator.getInstance().registerService(
          serviceName, Class.forName(serviceClassName).newInstance() );
    }
  }

  @Override
  protected void doPost(
    final HttpServletRequest request,
    final HttpServletResponse response
  ) throws ServletException, IOException {

    request.setCharacterEncoding("UTF-8");

    final Map<String,Object> responseData =
        new LinkedHashMap<String, Object>();

    final RPCHandler handler = createHandler();
    final JSONParser parser = new JSONParser(request.getReader(), handler);

    try {
      parser.parseAny();
      beforeCall(request, handler.getTargetMethod() );
      final Object result = handler.call();
      responseData.put(STATUS_KEY, STATUS_SUCCESS);
      responseData.put(RESULT_KEY, result);
    } catch(Exception e) {
      responseData.put(STATUS_KEY, STATUS_FAILURE);
      responseData.put(MESSAGE_KEY, e.getMessage() );
      logger.error(e.getMessage(), e);
    } finally {
      parser.close();
    }

    response.setContentType("application/json;charset=UTF-8");
    final JSONWriter out = new JSONWriter(response.getWriter() );
    try {
      out.writeAny(responseData);
    } finally {
      out.close();
    }
  }

  protected RPCHandler createHandler() {
    return new RPCHandler();
  }

  /**
   * Override this method to security check.
   * @param request
   * @param targetMethod
   */
  protected void beforeCall(
      HttpServletRequest request, Method targetMethod) throws Exception {
    logger.debug("beforeCall " + targetMethod);
  }
}
