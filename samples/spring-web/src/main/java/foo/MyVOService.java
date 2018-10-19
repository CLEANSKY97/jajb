package foo;

import org.springframework.stereotype.Service;

import com.d_project.jajb.rpc.Callable;

@Service("MyVOService")
public class MyVOService {
  @Callable
  public MyVO helloVO(MyVO vo) {
    vo.setMessage("hello," + vo.getMessage() +
        "," + vo.getNotSerializable() );
    vo.setNotSerializable("Can you hear me?");
    return vo;
  }
}
