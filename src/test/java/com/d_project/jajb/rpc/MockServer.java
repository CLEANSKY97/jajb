package com.d_project.jajb.rpc;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class MockServer implements InvocationHandler {

  private Object reqProxy;
  private Object resProxy;

  private StringReader in = null;
  private StringWriter out = null;

  private String requestData = "";

  public MockServer() throws Exception {
    reqProxy = Proxy.newProxyInstance(getClass().getClassLoader(),
        new Class[] { HttpServletRequest.class }, this);
    resProxy = Proxy.newProxyInstance(getClass().getClassLoader(),
        new Class[] { HttpServletResponse.class }, this);
  }

  public void setRequestData(String requestData) {
    this.requestData = requestData;
  }

  public String getResponseData() {
    return out.toString();
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
      } else {
        throw new RuntimeException("not implemented:" + method.getName() );
      }
    } else if (ServletResponse.class.
        isAssignableFrom(method.getDeclaringClass() ) ) {
      if (method.getName().equals("setContentType") ) {
        return null;
      } else if (method.getName().equals("getWriter") ) {
        if (out != null) {
          throw new RuntimeException("already got.");
        }
        out = new StringWriter();
        return new PrintWriter(out);
      } else {
        throw new RuntimeException("not implemented:" + method.getName() );
      }
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
