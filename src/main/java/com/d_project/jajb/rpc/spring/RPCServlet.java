package com.d_project.jajb.rpc.spring;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.d_project.jajb.rpc.AbstractRPCServlet;
import com.d_project.jajb.rpc.RPCHandler;
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
  private SecurityHandler securityHandler;

  public RPCServlet() {
  }

  @Override
  public void init(final ServletConfig config) throws ServletException {

    super.init(config);
    appContext = WebApplicationContextUtils.
        getRequiredWebApplicationContext(getServletContext() );

    securityHandler = appContext.getBean(SecurityHandler.class);
  }

  @Override
  protected RPCHandler createHandler() {
    return new RPCHandler(serviceProvider);
  }

  @Override
  protected boolean isApplicationException(final Exception e) {
    return false;
  }

  @Override
  protected SecurityHandler getSecurityHandler() {
    return securityHandler;
  }

}
