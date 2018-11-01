package ws;

import java.util.logging.Logger;

import com.d_project.jajb.rpc.Callable;

public class MyServerService {
  private static final Logger logger =
      Logger.getLogger(MyServerService.class.getName() );
  public final MyClientService clientService;
  public MyServerService(final MyClientService clientService) {
    this.clientService = clientService;
  }
  @Callable
  public void login(MyVO vo) {
   logger.info("login called."); 
   clientService.login(vo);
  }
}
