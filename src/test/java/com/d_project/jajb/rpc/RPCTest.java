package com.d_project.jajb.rpc;

import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpServlet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.d_project.jajb.JSON;
import com.d_project.jajb.JSONSerializable;
import com.d_project.jajb.TestVO;
import com.d_project.jajb.TestVO2;
import com.d_project.jajb.TestVO3;

/**
 * RPCTest
 * @author Kazuhiko Arase
 */
public class RPCTest {

  @Before
  public void prepare() throws Exception {
    ServiceLocator.getInstance().
      registerService("TestService", new TestService() );
  }

  @Test
  public void test() throws Exception {

    TestVO reqVO = new TestVO();
    reqVO.setGroup(new TestVO2() );
    reqVO.setItems(Arrays.asList(new TestVO3(), new TestVO3() ) );

    reqVO.setStr("test!");
    reqVO.setNum(3);
    reqVO.getGroup().setS1("one");
    reqVO.getGroup().setS2("two");
    reqVO.getItems().get(0).setF1("three");
    reqVO.getItems().get(1).setF1("four");

    MockServer mock = new MockServer();
    mock.setRequestData(JSON.stringify(ObjectUtil.asList(
        ObjectUtil.asMap(
            "serviceName", "TestService",
            "methodName", "test"),
        ObjectUtil.asList(1, 2, reqVO) ) ) );

    HttpServlet servlet = new RPCServlet();
    servlet.service(mock.getRequest(), mock.getResponse() );

    ResVO resVO = JSON.parse(mock.getResponseData(), ResVO.class);
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
