package com.d_project.jajb.rpc;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

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
  private static final String STATUS_FAILURE = "failure";

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

  protected void loadServices(final ServletConfig config) throws Exception {
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
  @SuppressWarnings("unchecked")
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

      final List<Object> params = (List<Object>)handler.getLastData();

      beforeCall(request, (Map<String,Object>)params.get(0),
          handler.getTargetMethod() );

      final Object result = handler.call();
      responseData.put(STATUS_KEY, STATUS_SUCCESS);
      responseData.put(RESULT_KEY, result);

    } catch(Exception e) {

      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      responseData.put(STATUS_KEY, STATUS_FAILURE);
      responseData.put(MESSAGE_KEY, e.getMessage() );
      logger.error(e.getMessage(), e);

    } finally {
      parser.close();
    }

    if (responseData.get(STATUS_KEY).equals(STATUS_SUCCESS) &&
        responseData.get(RESULT_KEY) instanceof Node) {

      // raw xml
      response.setContentType("application/xml;charset=UTF-8");
      final OutputStream out = new BufferedOutputStream(
          response.getOutputStream() );
      try {
        final Transformer tf = TransformerFactory.
            newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.transform(
            new DOMSource( (Node)responseData.get(RESULT_KEY) ),
            new StreamResult(out) );
      } catch(TransformerException e) {
        throw new IOException(e);
      } finally {
        out.close();
      }
      return;

    } else if (responseData.get(STATUS_KEY).equals(STATUS_SUCCESS) &&
        responseData.get(RESULT_KEY) instanceof InputStream) {

      // raw stream
      response.setContentType("application/octet-stream");
      final OutputStream out = new BufferedOutputStream(
          response.getOutputStream() );
      try {
        final InputStream in = (InputStream)responseData.get(RESULT_KEY);
        try {
          final byte[] buf = new byte[8192];
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
      return;
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
      final HttpServletRequest request,
    final Map<String,Object> opts,
    final Method targetMethod
  ) throws Exception {
    if (logger.isDebugEnabled() ) {
      logger.debug("beforeCall " + opts + " - " + targetMethod);
    }
  }
}
