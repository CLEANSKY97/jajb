package com.d_project.jajb.websocket;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * WSConfigurator
 * @author Kazuhiko Arase
 */
public class WSConfigurator extends ServerEndpointConfig.Configurator {

  @Override
  public void modifyHandshake(
    final ServerEndpointConfig config,
    final HandshakeRequest request,
    final HandshakeResponse response
  ) {
    config.getUserProperties().put("request", request);
  }
}
