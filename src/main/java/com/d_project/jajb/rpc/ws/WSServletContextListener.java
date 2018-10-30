package com.d_project.jajb.rpc.ws;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

import com.d_project.jajb.JSON;


/**
 * WSServletContextListener
 * @author Kazuhiko Arase
 */
public class WSServletContextListener 
implements ServletContextListener {

  private final Logger logger = Logger.getLogger(getClass().getName() );

  @SuppressWarnings("unchecked")
  @Override
  public void contextInitialized(final ServletContextEvent event) {
    logger.info("contextInitialized");
    final ServletContext servletContext = event.getServletContext();

    final ServerContainer serverContainer = 
        (ServerContainer)servletContext.getAttribute(
        "javax.websocket.server.ServerContainer");

    List<Map<String, String>> endpoints;
    try {
      endpoints =
          (List<Map<String, String>>)JSON.
          parse(servletContext.getInitParameter("ws.config") );
    } catch(RuntimeException e) {
      throw e;
    } catch(Exception e) {
      throw new RuntimeException(e);
    }

    for (final Map<String, String> ep : endpoints) {
      try {
        final String path = ep.get("path");
        final String factory = ep.get("factory");
        logger.info("register endpoint " +
            path + " - " + factory);
        final ServerEndpointConfig config = ServerEndpointConfig.Builder.
            create(WSEndpoint.class, path).
            configurator(new WSConfigurator() ).
            build();
        config.getUserProperties().put("servletContext", servletContext);
        config.getUserProperties().put("factory", factory);
        serverContainer.addEndpoint(config);
      } catch(DeploymentException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public void contextDestroyed(final ServletContextEvent event) {
    logger.info("contextDestroyed");
  }
}
