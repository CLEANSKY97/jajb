package foo;

import com.d_project.jajb.rpc.Callable;

public class MyVOService {
  @Callable
  public MyVO helloVO(MyVO vo) {
    vo.setMessage("hello," + vo.getMessage() +
        "," + vo.getNotSerializable() );
    vo.setNotSerializable("Can you hear me?");
    return vo;
  }
}
