package com.d_project.jajb.rpc;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.d_project.jajb.JSON;
import com.d_project.jajb.JSONSerializable;
import com.d_project.jajb.TestVO;
import com.d_project.jajb.TestVO2;
import com.d_project.jajb.TestVO3;


public class RPCTest {

  protected static MockServer server;

  @BeforeClass
  public static void prepare() throws Exception {
    server = new MockServer(new RPCServlet() );
  }

  @Test
  public void test0() throws Exception {
    server.setRequestData(JSON.stringify(ObjectUtil.asList(
        ObjectUtil.asMap(
            "serviceName", "TestService",
            "methodName", "test0"),
        ObjectUtil.asList() ) ) );

    server.doService();
  }

  @Test
  public void test1() throws Exception {
    server.setRequestData(JSON.stringify(ObjectUtil.asList(
        ObjectUtil.asMap(
            "serviceName", "TestService",
            "methodName", "test1"),
        ObjectUtil.asList(12) ) ) );

    server.doService();
  }

  @Test
  public void echo() throws Exception {
    server.setRequestData(JSON.stringify(ObjectUtil.asList(
        ObjectUtil.asMap(
            "serviceName", "TestService",
            "methodName", "echo"),
        ObjectUtil.asList("hello!") ) ) );

    server.doService();
  }

  @Test
  public void test2() throws Exception {
    server.setRequestData(JSON.stringify(ObjectUtil.asList(
        ObjectUtil.asMap(
            "serviceName", "TestService",
            "methodName", "test2"),
        ObjectUtil.asList(34, 56) ) ) );

    server.doService();
  }

  @Test
  public void testMixed() throws Exception {

    final TestVO reqVO = new TestVO();
    reqVO.setGroup(new TestVO2() );
    reqVO.setItems(Arrays.asList(new TestVO3(), new TestVO3() ) );

    reqVO.setStr("test!");
    reqVO.setNum(3);
    reqVO.getGroup().setS1("one");
    reqVO.getGroup().setS2("two");
    reqVO.getItems().get(0).setF1("three");
    reqVO.getItems().get(1).setF1("four");

    server.setRequestData(JSON.stringify(ObjectUtil.asList(
        ObjectUtil.asMap(
            "serviceName", "TestService",
            "methodName", "testMixed"),
        ObjectUtil.asList(1, 2, reqVO) ) ) );

    server.doService();

    final ResVO resVO = JSON.parse(server.getResponseData(), ResVO.class);
    Assert.assertEquals("success", resVO.getStatus() );
    Assert.assertEquals(6, resVO.getResult().getNum() );
    Assert.assertEquals("one", resVO.getResult().getGroup().getS1() );
  }

  @JSONSerializable
  public static class ResVO {
    @JSONSerializable
    private String status;
    @JSONSerializable
    private TestVO result;
    public String getStatus() {
      return status;
    }
    public void setStatus(String status) {
      this.status = status;
    }
    public TestVO getResult() {
      return result;
    }
    public void setResult(TestVO result) {
      this.result = result;
    }
  }
}
