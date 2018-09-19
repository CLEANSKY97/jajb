package com.d_project.jajb.rpc;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

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

  private static final String SECURITY_HANDLER_KEY = "security-handler";
  private static final String DEFAULT_SECURITY_HANDLER_CLASS =
      com.d_project.jajb.rpc.DefaultSecurityHandler.class.getName();
  private static final String APPLICATION_EXCEPTIONS_KEY =
      "application-exceptions";

  private static final String STATUS_KEY = "status";
  private static final String STATUS_SUCCESS = "success";
  private static final String STATUS_FAILURE = "failure";

  private static final String RESULT_KEY = "result";
  private static final String MESSAGE_KEY = "message";

  protected static final Logger logger =
      LoggerFactory.getLogger(RPCServlet.class);

  private Set<Class<?>> applicationExceptions;

  private SecurityHandler securityHandler;

  @Override
  public void init(final ServletConfig config) throws ServletException {

    super.init(config);

    try {

      parseApplicationExceptions(config);

      initSecurityHandler(config);

      loadServices(config);

    } catch(RuntimeException e) {
      throw e;
    } catch(ServletException e) {
      throw e;
    } catch(Exception e) {
      throw new ServletException(e);
    }
  }

  protected void parseApplicationExceptions(
      final ServletConfig config) throws Exception {
    applicationExceptions = new HashSet<Class<?>>();
    final String appExceptions = config.
        getInitParameter(APPLICATION_EXCEPTIONS_KEY);
    if (appExceptions != null) {
      for (final String className : appExceptions.split("\\s+") ) {
        if (className.length() > 0) {
          applicationExceptions.add(Class.forName(className) );
        }
      }
    }
  }

  protected void initSecurityHandler(
      final ServletConfig config) throws Exception {
    final String securityHandlerClass =
        config.getInitParameter(SECURITY_HANDLER_KEY);
    securityHandler = (SecurityHandler)Class.forName(
        securityHandlerClass != null?
            securityHandlerClass : DEFAULT_SECURITY_HANDLER_CLASS).
        newInstance();
    securityHandler.init(config);
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

      if (securityHandler.isAuthorized(request,
          (Map<String,Object>)params.get(0),
          handler.getTargetMethod() ) ) {

        final Object result = handler.call();
        responseData.put(STATUS_KEY, STATUS_SUCCESS);
        responseData.put(RESULT_KEY, result);

      } else {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        responseData.put(STATUS_KEY, STATUS_FAILURE);
        responseData.put(MESSAGE_KEY, "forbidden");
      }

    } catch(Exception e) {

      // extract InvocationTargetException.
      if (e instanceof InvocationTargetException) {
        final Throwable t = ( (InvocationTargetException)e).
            getTargetException();
        if (t instanceof Exception) {
          e = (Exception)t;
        }
      }

      if (isApplicationException(e) ) {

        // application exception returns message with normal state(200 OK)

        if (logger.isDebugEnabled() ) {
          logger.error(e.getMessage(), e);
        }

        responseData.put(STATUS_KEY, STATUS_FAILURE);
        responseData.put(MESSAGE_KEY, e.getMessage() );

      } else {

        logger.error(e.getMessage(), e);

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        responseData.put(STATUS_KEY, STATUS_FAILURE);
        responseData.put(MESSAGE_KEY, e.getMessage() );
      }

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

  protected boolean isApplicationException(final Exception e) {
    for (final Class<?> c : applicationExceptions) {
      if (c.isAssignableFrom(e.getClass() ) ) {
        return true;
      }
    }
    return false;
  }

  protected RPCHandler createHandler() {
    
    return new RPCHandler();
  }
}
