package com.d_project.jajb.rpc;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * SecurityHandler
 * @author Kazuhiko Arase
 */
public interface SecurityHandler {

  /**
   * initialize the SecurityHandler
   * @param config
   * @throws ServletException
   */
  void init(ServletConfig config) throws ServletException;

  /**
   * return true if authorized.
   * @param request
   * @param opts
   * @param targetMethod
   * @return
   * @throws ServletException
   */
  boolean isAuthorized(
    HttpServletRequest request,
    Map<String,Object> opts,
    Method targetMethod) throws ServletException;
}
