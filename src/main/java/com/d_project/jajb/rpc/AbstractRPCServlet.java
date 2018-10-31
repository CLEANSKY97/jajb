package com.d_project.jajb.rpc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Node;

import com.d_project.jajb.JSONParser;
import com.d_project.jajb.JSONWriter;

/**
 * AbstractRPCServlet
 * @author Kazuhiko Arase
 */
@SuppressWarnings("serial")
public abstract class AbstractRPCServlet extends HttpServlet {

  private static final String STATUS_KEY = "status";
  private static final String STATUS_SUCCESS = "success";
  private static final String STATUS_FAILURE = "failure";

  private static final String RESULT_KEY = "result";
  private static final String MESSAGE_KEY = "message";

  protected static final Logger logger =
      Logger.getLogger(RPCServlet.class.getName() );

  private Class<?>[] applicationExceptions = null;

  protected AbstractRPCServlet() {
  }

  @Override
  protected void doPost(
    final HttpServletRequest request,
    final HttpServletResponse response
  ) throws ServletException, IOException {

    final RPCHandler handler = createHandler();

    {
      request.setCharacterEncoding("UTF-8");
      final JSONParser parser = new JSONParser(request.getReader(), handler);
      try {
        parser.parseAny();
      } finally {
        parser.close();
      }
    }

    final Map<String,Object> responseData =
        new LinkedHashMap<String, Object>();

    try {

      if (getSecurityHandler().isAuthorized(request,
          handler.getOpts(), handler.getTargetMethod() ) ) {

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

        if (logger.isLoggable(Level.FINEST) ) {
          logger.log(Level.FINEST, e.getMessage(), e);
        }

        responseData.put(STATUS_KEY, STATUS_FAILURE);
        responseData.put(MESSAGE_KEY, e.getMessage() );

      } else {

        logger.log(Level.SEVERE, e.getMessage(), e);

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        responseData.put(STATUS_KEY, STATUS_FAILURE);
        responseData.put(MESSAGE_KEY, e.getMessage() );
      }
    }

    if (responseData.get(STATUS_KEY).equals(STATUS_SUCCESS) ) {
      final Object result = responseData.get(RESULT_KEY);
      if (result instanceof Node) {
        new DOMResultHandler( (Node)result).handle(request, response);
        return;
      } else if (result instanceof ResultHandler) {
        ((ResultHandler)result).handle(request, response);
        return;
      }
    }

    final String encoding = "UTF-8";
    response.setContentType("application/json;charset=" + encoding);

    GZIPUtil.output(request, response, new GZIPUtil.OutputHandler() {
      @Override
      @SuppressWarnings("resource")
      public void writeTo(final OutputStream out) throws IOException {
        final JSONWriter writer = new JSONWriter(
            new OutputStreamWriter(out, encoding) );
        try {
          writer.writeAny(responseData);
        } finally {
          writer.flush();
        }
      }
    });
  }

  protected boolean isApplicationException(final Exception e) {

    if (this.applicationExceptions == null) {
      final List<Class<?>> applicationExceptions = getApplicationExceptions();
      this.applicationExceptions = applicationExceptions.toArray(
          new Class<?>[applicationExceptions.size()] );
    }

    for (final Class<?> c : applicationExceptions) {
      if (c.isAssignableFrom(e.getClass() ) ) {
        return true;
      }
    }
    return false;
  }

  protected final RPCHandler createHandler() {
    return new DefaultRPCHandler(getServiceProvider() );
  }

  protected abstract ServiceProvider getServiceProvider();
  protected abstract List<Class<?>> getApplicationExceptions();
  protected abstract SecurityHandler getSecurityHandler();

}
