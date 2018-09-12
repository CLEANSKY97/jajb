package com.d_project.jajb;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class Mock implements InvocationHandler {

  private Object reqProxy;
  private Object resProxy;

  public Mock() throws Exception {
    reqProxy = Proxy.newProxyInstance(
        getClass().getClassLoader(),
        new Class[] {
            HttpServletRequest.class },
        this);
    resProxy = Proxy.newProxyInstance(
        getClass().getClassLoader(),
        new Class[] {
            HttpServletResponse.class },
        this);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args)
      throws Throwable {
    if (ServletRequest.class.
        isAssignableFrom(method.getDeclaringClass() ) ) {
      if (method.getName().equals("getMethod") ) {
        return "POST";
      }
      throw new RuntimeException("not implemented.");
    } else if (HttpServletResponse.class.
        isAssignableFrom(method.getDeclaringClass() ) ) {

      throw new RuntimeException("not implemented.");
    } else {
      return method.invoke(Mock.this);
    }
  }

  public HttpServletRequest getRequest() {
    return (HttpServletRequest)reqProxy;
  }
  public HttpServletResponse getResponse() {
    return (HttpServletResponse)resProxy;
  }
}
