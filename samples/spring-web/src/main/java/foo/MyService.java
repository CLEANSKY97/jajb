package foo;

import org.springframework.stereotype.Service;

import com.d_project.jajb.rpc.Callable;

@Service("MyService")
public class MyService {
  @Callable
  public int add(int a, int b) { return a + b; }
}
