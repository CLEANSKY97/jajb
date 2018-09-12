package com.d_project.jajb;

import javax.servlet.http.HttpServlet;

import org.junit.Test;

import com.d_project.jajb.rpc.RPCServlet;

public class MockTest {
  @Test
  public void test() throws Exception {
    Mock mock = new Mock();
    HttpServlet servlet = new RPCServlet();

    servlet.service(mock.getRequest(), mock.getResponse() );
  }
}