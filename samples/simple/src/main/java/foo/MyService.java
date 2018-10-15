package foo;

import com.d_project.jajb.rpc.Callable;

public class MyService {
  @Callable
  public int add(int a, int b) { return a + b; }
}
