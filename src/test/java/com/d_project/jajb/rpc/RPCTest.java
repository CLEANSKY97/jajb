package com.d_project.jajb.rpc;

import javax.servlet.http.HttpServlet;

import org.junit.Before;
import org.junit.Test;

import com.d_project.jajb.JSON;
import com.d_project.jajb.TestVO;
import com.d_project.jajb.rpc.RPCServlet;

public class RPCTest {

  @Before
  public void prepare() throws Exception {
    ServiceLocator.getInstance().registerService("TestService", new TestService() );
  }

  @Test
  public void test() throws Exception {
    TestVO reqVO = new TestVO();
    System.out.println(reqVO);
    MockServer mock = new MockServer();
    mock.setRequestData(JSON.stringify(ObjectUtil.asList(
        ObjectUtil.asMap(
            "serviceName", "TestService",
            "methodName", "test"),
        ObjectUtil.asList(1, 2, reqVO) ) ) );
    HttpServlet servlet = new RPCServlet();
    servlet.service(mock.getRequest(), mock.getResponse() );
  }
}