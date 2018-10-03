package com.d_project.jajb.rpc;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DefaultSecurityHandler
 * @author Kazuhiko Arase
 * accept any body.
 */
public class DefaultSecurityHandler implements SecurityHandler {

  protected static final Logger logger =
      LoggerFactory.getLogger(DefaultSecurityHandler.class);

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
    if (logger.isDebugEnabled() ) {
      logger.debug("isAuthorized " + opts + " - " + targetMethod);
    }
    return true;
  }
}
