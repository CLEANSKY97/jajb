package com.d_project.jajb.rpc;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * DefaultSecurityHandler
 * @author Kazuhiko Arase
 * accept any body.
 */
public class DefaultSecurityHandler implements SecurityHandler {

  protected static final Logger logger =
      Logger.getLogger(DefaultSecurityHandler.class.getName() );

  public DefaultSecurityHandler() {
  }

  @Override
  public void init(final ServletConfig config) throws ServletException {
  }

  @Override
  public boolean isAuthorized(
      final HttpServletRequest request,
      final Map<String, Object> opts,
      final Method targetMethod) {
    if (logger.isLoggable(Level.FINEST) ) {
      logger.finest("isAuthorized " + opts + " - " + targetMethod);
    }
    // allow all.
    return true;
  }
}
