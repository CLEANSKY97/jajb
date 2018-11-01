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
import javax.websocket.Session;

import com.d_project.jajb.JSON;
import com.d_project.jajb.JSONField;
import com.d_project.jajb.JSONParser;
import com.d_project.jajb.JSONType;
import com.d_project.jajb.rpc.Callable;
import com.d_project.jajb.rpc.DefaultRPCHandler;
import com.d_project.jajb.rpc.RPCHandler;
import com.d_project.jajb.rpc.ServiceProvider;
import com.d_project.jajb.rpc.ws.IEndpoint;
import com.d_project.jajb.rpc.ws.IEndpointFactory;
import com.d_project.jajb.rpc.ws.IWSEndpointConfig;

// prototype
public class MyEndpointFactory
implements IEndpointFactory, IEndpoint, InvocationHandler {

  private static final Logger logger =
      Logger.getLogger(MyEndpointFactory.class.getName() );

  private Session session;
  private ClientService clientService;

  @Override
  public IEndpoint createEndpoint(final IWSEndpointConfig config) {

    try {
      clientService = (ClientService)Proxy.newProxyInstance(getClass().getClassLoader(),
          new Class[] { ClientService.class }, this);
    } catch(RuntimeException e) {
      throw e;
    } catch(Exception e) {
      throw new RuntimeException(e);
    }

    session = config.getSession();
    return this;
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
    return new ServiceProvider() {
      @Override
      public Object getServiceByName(String serviceName) {
        return MyEndpointFactory.this;
      }
    };
  }

  @Callable
  public void login(MyVO vo) {
   logger.info("login called."); 
   clientService.login(vo);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args)
      throws Throwable {

    final Map<String,Object> opts = new LinkedHashMap<String,Object>();
    opts.put("methodName", method.getName() );

    session.getBasicRemote().sendText(
        JSON.stringify(Arrays.asList(opts, args) ) );
    return null;
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
