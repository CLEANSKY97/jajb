package com.d_project.jajb.websocket;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

/**
 * WSEndpoint
 * @author Kazuhiko Arase
 */
public abstract class WSEndpoint extends Endpoint {

  private static final ConcurrentHashMap<String,Object> global;
  private static final String SESSION_MAP_KEY = "sessionMap";

  protected static final Logger logger;

  static {
    global = new ConcurrentHashMap<String,Object>();
    global.put(SESSION_MAP_KEY, new ConcurrentHashMap<String,Session>() );
    logger = Logger.getLogger(WSEndpoint.class.getName() );
  }

  protected static ConcurrentHashMap<String,Object> getGlobal() {
    return global;
  }

  @SuppressWarnings("unchecked")
  protected static ConcurrentHashMap<String,Session> getSessionMap() {
    return (ConcurrentHashMap<String,Session>)getGlobal().
        get(SESSION_MAP_KEY);
  }

  protected WSEndpoint() {
  }

  @Override
  public void onOpen(final Session session, final EndpointConfig config) {
    session.addMessageHandler(new MessageHandler.Whole<String>() {
      @Override
      public void onMessage(final String message) {
        WSEndpoint.this.onMessage(session, message);
      }
    });
    getSessionMap().put(session.getId(), session);
  }

  @Override
  public void onClose(final Session session, final CloseReason closeReason) {
    getSessionMap().remove(session.getId() );
  }

  @Override
  public void onError(final Session session, final Throwable t) {
    if (t instanceof IOException) {
      // ignore
    } else {
      logger.log(Level.SEVERE, t.getMessage(), t);
    }
  }

  public void onMessage(Session session, String message) {
    try {
      // echo back.
      session.getBasicRemote().sendText(message);
    } catch(IOException e) {
      logger.log(Level.SEVERE, e.getMessage(), e);
    }
  }
}
