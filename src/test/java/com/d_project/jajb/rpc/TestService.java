package com.d_project.jajb.rpc;

import com.d_project.jajb.TestVO;

public class TestService {

  @Callable
  public TestVO test(int a, int b, TestVO vo) {
    vo.setNum(vo.getNum() + a + b);
    return vo;
  }
}
