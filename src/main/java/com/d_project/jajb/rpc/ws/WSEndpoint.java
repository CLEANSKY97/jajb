package com.d_project.jajb.rpc.ws;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
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

      final IWSEndpointConfig endpointConfig = new IWSEndpointConfig() {
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

      final IEndpoint endpoint =
          (IEndpoint)Class.forName(factory).newInstance();
      endpoint.init(endpointConfig);
      return endpoint;

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
        try {
          context.getEndpoint().onMessage(message);
        } catch(RuntimeException e) {
          throw e;
        } catch(Exception e) {
          throw new RuntimeException(e);
        }
      }
    });
    getContextMap().put(session.getId(), context);
    try {
      context.getEndpoint().onOpen(config);
    } catch(RuntimeException e) {
      throw e;
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onClose(Session session, CloseReason closeReason) {
    final Context context = getContextMap().get(session.getId() );
    try {
      context.getEndpoint().onClose(closeReason);
    } catch(RuntimeException e) {
      throw e;
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
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
