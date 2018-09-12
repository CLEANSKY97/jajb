package com.d_project.jajb.rpc;

import java.util.HashMap;
import java.util.Map;

/**
 * ServiceLocator
 * @author Kazuhiko Arase
 */
public class ServiceLocator {
  private static ServiceLocator instance;
  public static ServiceLocator getInstance() {
    if (instance == null) {
      instance = new ServiceLocator();
    }
    return instance;
  }
  private Map<String, Object> services;
  private boolean freezed;
  protected ServiceLocator() {
    services = new HashMap<String, Object>();
    freezed = false;
  }
  public void registerService(
      final String serviceName, final Object service) {
    if (freezed) {
      throw new RuntimeException("freezed.");
    }
    if (services.containsKey(serviceName) ) {
      throw new RuntimeException(
          "Service already registered : " + serviceName);
    }
    services.put(serviceName, service);
  }
  public Object getService(final String serviceName) {
    if (!freezed) {
      freezed = true;
    }
    final Object service = services.get(serviceName);
    if (service == null) {
      throw new RuntimeException("Service not found : " + serviceName);
    }
    return service;
  }
}
