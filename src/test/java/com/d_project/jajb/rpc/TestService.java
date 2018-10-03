package com.d_project.jajb.rpc;

import com.d_project.jajb.TestVO;

public class TestService {

  @Callable
  public void test0() {
    // no args, no return.
  }

  @Callable
  public String[] test1(int a) {
    return new String[] { "a", "b" };
  }

  @Callable
  public String echo(String a) {
    return a;
  }

  @Callable
  public Object[] test2(int a, int b) {
    return new Object[] { "a", 1 };
  }

  @Callable
  public long[] testSwap(int[] a) {
    // array in, array out
    return new long[] { a[1], a[0] };
  }

  @Callable
  public TestVO testMixed(int a, int b, TestVO vo) {
    vo.setNum(vo.getNum() + a + b);
    return vo;
  }
}
