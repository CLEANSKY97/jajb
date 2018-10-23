package com.d_project.jajb.rpc.spring;

import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.d_project.jajb.rpc.AbstractRPCServlet;
import com.d_project.jajb.rpc.SecurityHandler;
import com.d_project.jajb.rpc.ServiceProvider;

/**
 * RPCServlet
 * @author Kazuhiko Arase
 */
@SuppressWarnings("serial")
public class RPCServlet extends AbstractRPCServlet {

  private final ServiceProvider serviceProvider = new ServiceProvider() {
    @Override
    public Object getServiceByName(String serviceName) {
      return appContext.getBean(serviceName);
    }
  };

  private ApplicationContext appContext;
  private List<Class<?>> applicationExceptions;
  private SecurityHandler securityHandler;

  public RPCServlet() {
  }

  @Override
  @SuppressWarnings("unchecked")
  public void init(final ServletConfig config) throws ServletException {

    super.init(config);

    appContext = WebApplicationContextUtils.
        getRequiredWebApplicationContext(getServletContext() );
    applicationExceptions =
        (List<Class<?>>)appContext.getBean("applicationExceptions");
    securityHandler = appContext.getBean(SecurityHandler.class);
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
