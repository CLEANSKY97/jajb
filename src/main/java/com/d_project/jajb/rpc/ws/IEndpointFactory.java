package com.d_project.jajb.rpc.ws;

/**
 * IEndpointFactory
 * @author Kazuhiko Arase
 */
public interface IEndpointFactory {
  IEndpoint createEndpoint(WSEndpointConfig endpointConfig);
}
