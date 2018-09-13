package com.d_project.jajb.rpc;

import com.d_project.jajb.TestVO;

public class TestService {

  @Callable
  public String[] test0() {
    return new String[] { "a", "b" };
  }

  @Callable
  public String[] test1(int a) {
    return new String[] { "a", "b" };
  }

  @Callable
  public String[] test2(int a, int b) {
    return new String[] { "a", "b" };
  }

  @Callable
  public TestVO testMixed(int a, int b, TestVO vo) {
    vo.setNum(vo.getNum() + a + b);
    return vo;
  }
}
