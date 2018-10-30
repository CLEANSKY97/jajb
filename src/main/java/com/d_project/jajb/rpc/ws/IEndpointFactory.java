package com.d_project.jajb.rpc.ws;

import java.util.Map;

/**
 * IEndpointFactory
 * @author Kazuhiko Arase
 */
public interface IEndpointFactory {
  IEndpoint createEndpoint(Map<String,Object> endpointConfig);
}
