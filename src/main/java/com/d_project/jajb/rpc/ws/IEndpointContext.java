package com.d_project.jajb.rpc.ws;

import java.util.Map;

/**
 * IEndpointContext
 * @author Kazuhiko Arase
 */
public interface IEndpointContext {
  IEndpoint createEndpoint(Map<String,Object> endpointConfig);
}
