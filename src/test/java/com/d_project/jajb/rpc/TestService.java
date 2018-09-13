package com.d_project.jajb.rpc;

import com.d_project.jajb.TestVO;

/**
 * TestService
 * @author Kazuhiko Arase
 */
public class TestService {
  public TestVO test(int a, int b, TestVO vo) {
    vo.setNum(vo.getNum() + a + b);
    return vo;
  }
}