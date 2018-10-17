package com.d_project.jajb.rpc;

/**
 * ServiceProvider
 * @author Kazuhiko Arase
 */
public interface ServiceProvider {
  Object getServiceByName(String serviceName);
}
