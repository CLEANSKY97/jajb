package ws;

import java.io.StringReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;

import com.d_project.jajb.JSON;
import com.d_project.jajb.JSONField;
import com.d_project.jajb.JSONParser;
import com.d_project.jajb.JSONType;
import com.d_project.jajb.rpc.Callable;
import com.d_project.jajb.rpc.DefaultRPCHandler;
import com.d_project.jajb.rpc.RPCHandler;
import com.d_project.jajb.rpc.ServiceProvider;
import com.d_project.jajb.rpc.ws.IEndpoint;
import com.d_project.jajb.rpc.ws.IWSEndpointConfig;

// prototype
public class MyEndpoint implements IEndpoint {

  private static final Logger logger =
      Logger.getLogger(MyEndpoint.class.getName() );

  private ServiceProvider serviceProvider =  new ServiceProvider() {
    @Override
    public Object getServiceByName(String serviceName) {
      return serverService;
    }
  };

  private ServerService serverService;

  @Override
  public void init(final IWSEndpointConfig config) {

    try {

      final ClientService clientService = (ClientService)Proxy.
          newProxyInstance(getClass().getClassLoader(),
          new Class[] { ClientService.class },
          new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {

              final Map<String,Object> opts = new LinkedHashMap<String,Object>();
              opts.put("methodName", method.getName() );

              config.getSession().getBasicRemote().sendText(
                  JSON.stringify(Arrays.asList(opts, args) ) );
              return null;
            }
          });
      serverService = new ServerService(clientService);

    } catch(RuntimeException e) {
      throw e;
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onOpen(EndpointConfig config) {
    logger.info("onOpen:" + config);
  }

  @Override
  public void onClose(CloseReason closeReason) {
    logger.info("onClose:" + closeReason);
  }

  @Override
  public void onMessage(String message) {

    logger.info("onMessage:" + message);

    try {

      final RPCHandler handler = createHandler();

      {
        final JSONParser parser = new JSONParser(
            new StringReader(message), handler);
        try {
          parser.parseAny();
        } finally {
          parser.close();
        }
      }

      handler.call();

    } catch(RuntimeException e) {
      throw e;
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected final RPCHandler createHandler() {
    return new DefaultRPCHandler(getServiceProvider() );
  }

  protected ServiceProvider getServiceProvider() {
    return serviceProvider;
  }

  public class ServerService {
    public final ClientService clientService;
    public ServerService(final ClientService clientService) {
      this.clientService = clientService;
    }
    @Callable
    public void login(MyVO vo) {
     logger.info("login called."); 
     clientService.login(vo);
    }
  }

  public interface ClientService {
    void login(MyVO vo);
  }

  @JSONType
  public static class MyVO {
    @JSONField
    private String f1;
    @JSONField
    private int f2;
    public String getF1() {
      return f1;
    }
    public void setF1(String f1) {
      this.f1 = f1;
    }
    public int getF2() {
      return f2;
    }
    public void setF2(int f2) {
      this.f2 = f2;
    }
  }
}
