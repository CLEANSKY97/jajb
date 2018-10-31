package com.d_project.jajb.rpc.ws;

import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;

/**
 * IWSEndpointConfig
 * @author Kazuhiko Arase
 */
public interface IWSEndpointConfig {
  Logger getLogger();
  Map<String,Object> getGlobal();
  Session getSession();
  ServletContext getServletContext();
  HandshakeRequest getRequest();
}
