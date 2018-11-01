package com.d_project.jajb.rpc.ws;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;

/**
 * IEndpoint
 * @author Kazuhiko Arase
 */
public interface IEndpoint {
  void init(IWSEndpointConfig endpointConfig) throws Exception;
  void onOpen(EndpointConfig config) throws Exception;
  void onClose(CloseReason closeReason) throws Exception;
  void onMessage(String message) throws Exception;
}
