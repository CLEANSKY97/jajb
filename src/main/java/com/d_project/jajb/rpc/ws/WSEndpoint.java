package com.d_project.jajb.rpc.ws;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;

/**
 * WSEndpoint
 * @author Kazuhiko Arase
 */
public class WSEndpoint extends Endpoint {

  private static final ConcurrentHashMap<String,Object> global;
  protected static final Logger logger;

  static {
    global = new ConcurrentHashMap<String,Object>();
    global.put("contextMap", new ConcurrentHashMap<String,Context>() );
    logger = Logger.getLogger(WSEndpoint.class.getName() );
  }

  public WSEndpoint() {
  }

  protected ConcurrentHashMap<String,Object> getGlobal() {
    return global;
  }

  @SuppressWarnings("unchecked")
  protected ConcurrentHashMap<String,Context> getContextMap() {
    return (ConcurrentHashMap<String,Context>)global.get("contextMap");
  }

  protected IEndpoint createEndpoint(
      final Session session, final EndpointConfig config) {
    try {

      final Map<String, Object> userProps = config.getUserProperties();
      final String factory = (String)userProps.get("factory");

      final WSEndpointConfig endpointConfig = new WSEndpointConfig() {
        @Override
        public Map<String, Object> getGlobal() {
          return global;
        }
        @Override
        public Logger getLogger() {
          return logger;
        }
        @Override
        public HandshakeRequest getRequest() {
          return (HandshakeRequest)userProps.get("request");
        }
        @Override
        public ServletContext getServletContext() {
          return (ServletContext)userProps.get("servletContext");
        }
        @Override
        public Session getSession() {
          return session;
        }
      };

      // clear properties.
      config.getUserProperties().clear();

      return ((IEndpointFactory)Class.forName(factory).newInstance() ).
          createEndpoint(endpointConfig);

    } catch(RuntimeException e) {
      throw e;
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onOpen(Session session, EndpointConfig config) {
    final Context context = new Context(session,
        createEndpoint(session, config) );
    session.addMessageHandler(new MessageHandler.Whole<String>() {
      @Override
      public void onMessage(String message) {
        context.getEndpoint().onMessage(message);
      }
    });
    getContextMap().put(session.getId(), context);
    context.getEndpoint().onOpen(config);
  }

  @Override
  public void onClose(Session session, CloseReason closeReason) {
    final Context context = getContextMap().get(session.getId() );
    context.getEndpoint().onClose(closeReason);
    getContextMap().remove(session.getId() );
  }

  @Override
  public void onError(Session session, Throwable t) {
    if (t instanceof IOException) {
      // ignore
    } else {
      logger.log(Level.SEVERE, t.getMessage(), t);
    }
  }
}
