package com.d_project.jajb.rpc;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * RPCServlet
 * @author Kazuhiko Arase
 */
@SuppressWarnings("serial")
public class RPCServlet extends AbstractRPCServlet {

  private static final String DEFAULT_SECURITY_HANDLER_CLASS =
      DefaultSecurityHandler.class.getName();

  private static final String SECURITY_HANDLER_KEY = "security-handler";
  private static final String APPLICATION_EXCEPTIONS_KEY =
      "application-exceptions";

  private SecurityHandler securityHandler;
  private Map<String, Object> services;
  private List<Class<?>> applicationExceptions;

  private final ServiceProvider serviceProvider = new ServiceProvider() {
    @Override
    public Object getServiceByName(String serviceName) {
      final Object service = services.get(serviceName);
      if (service == null) {
        throw new RuntimeException("Service not found : " + serviceName);
      }
      return service;
    }
  };

  public RPCServlet() {
  }

  @Override
  public void init(final ServletConfig config) throws ServletException {

    super.init(config);

    try {

      parseApplicationExceptions(config);

      initSecurityHandler(config);

      loadServices(config);

    } catch(RuntimeException e) {
      throw e;
    } catch(ServletException e) {
      throw e;
    } catch(Exception e) {
      throw new ServletException(e);
    }
  }

  protected void parseApplicationExceptions(
      final ServletConfig config) throws Exception {
    applicationExceptions = new ArrayList<Class<?>>();
    final String appExceptions = config.
        getInitParameter(APPLICATION_EXCEPTIONS_KEY);
    if (appExceptions != null) {
      for (final String className : appExceptions.split("\\s+") ) {
        if (className.length() > 0) {
          applicationExceptions.add(Class.forName(className) );
        }
      }
    }
  }

  protected void initSecurityHandler(
      final ServletConfig config) throws Exception {
    final String securityHandlerClass =
        config.getInitParameter(SECURITY_HANDLER_KEY);
    securityHandler = (SecurityHandler)Class.forName(
        securityHandlerClass != null?
            securityHandlerClass :
              DEFAULT_SECURITY_HANDLER_CLASS).newInstance();
    securityHandler.init(config);
  }

  protected void loadServices(final ServletConfig config) throws Exception {

    final String path = config.getInitParameter("services");
    logger.debug("loading services from " + path);
    final Properties serviceDefs = new Properties();
    final InputStream in = config.getServletContext().
        getResourceAsStream(path);
    try {
      serviceDefs.load(in);
    } finally {
      in.close();
    }

    services = new HashMap<String, Object>();
    for (final Entry<Object,Object> entry : serviceDefs.entrySet() ) {
      final String serviceName = (String)entry.getKey();
      final String serviceClassName = (String)entry.getValue();
      logger.debug(serviceName + " = " + serviceClassName);
      registerService(serviceName,
          Class.forName(serviceClassName).newInstance() );
    }
  }

  protected void registerService(
      final String serviceName, final Object service) {
    if (services.containsKey(serviceName) ) {
      throw new RuntimeException(
          "Service already registered : " + serviceName);
    }
    services.put(serviceName, service);
  }

  @Override
  protected ServiceProvider getServiceProvider() {
    return serviceProvider;
  }

  @Override
  protected List<Class<?>> getApplicationExceptions() {
    return applicationExceptions;
  }

  @Override
  protected SecurityHandler getSecurityHandler() {
    return securityHandler;
  }

}
