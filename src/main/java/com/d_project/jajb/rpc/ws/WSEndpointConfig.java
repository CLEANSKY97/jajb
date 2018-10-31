package com.d_project.jajb.rpc.ws;

import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;

/**
 * WSEndpointConfig
 * @author Kazuhiko Arase
 */
public interface WSEndpointConfig {
  Logger getLogger();
  Map<String,Object> getGlobal();
  Session getSession();
  ServletContext getServletContext();
  HandshakeRequest getRequest();
}
