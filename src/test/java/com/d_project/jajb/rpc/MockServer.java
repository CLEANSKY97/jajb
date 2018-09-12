package com.d_project.jajb.rpc;

import java.io.BufferedReader;
import java.io.StringReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class MockServer implements InvocationHandler {

  private Object reqProxy;
  private Object resProxy;

  public MockServer() throws Exception {
    reqProxy = Proxy.newProxyInstance(getClass().getClassLoader(),
        new Class[] { HttpServletRequest.class }, this);
    resProxy = Proxy.newProxyInstance(getClass().getClassLoader(),
        new Class[] { HttpServletResponse.class }, this);
  }

  private String requestData = "";
  private StringReader in = null;

  public void setRequestData(String requestData) {
    this.requestData = requestData;
  }


  @Override
  public Object invoke(Object proxy, Method method, Object[] args)
      throws Throwable {
    if (ServletRequest.class.
        isAssignableFrom(method.getDeclaringClass() ) ) {
      if (method.getName().equals("getMethod") ) {
        return "POST";
      } else if (method.getName().equals("setCharacterEncoding") ) {
        return null;
      } else if (method.getName().equals("getReader") ) {
        if (in != null) {
          throw new RuntimeException("already got.");
        }
        in = new StringReader(requestData);
        return new BufferedReader(in);
      }
      throw new RuntimeException("not implemented.");
    } else if (HttpServletResponse.class.
        isAssignableFrom(method.getDeclaringClass() ) ) {

      throw new RuntimeException("not implemented.");
    } else {
      return method.invoke(MockServer.this);
    }
  }

  public HttpServletRequest getRequest() {
    return (HttpServletRequest)reqProxy;
  }
  public HttpServletResponse getResponse() {
    return (HttpServletResponse)resProxy;
  }
}
