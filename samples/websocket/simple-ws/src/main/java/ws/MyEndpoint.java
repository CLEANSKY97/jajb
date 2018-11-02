package ws;

import java.io.StringReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;

import com.d_project.jajb.JSON;
import com.d_project.jajb.JSONParser;
import com.d_project.jajb.rpc.DefaultRPCHandler;
import com.d_project.jajb.rpc.RPCHandler;
import com.d_project.jajb.rpc.ServiceProvider;
import com.d_project.jajb.rpc.ws.IEndpoint;
import com.d_project.jajb.rpc.ws.IWSEndpointConfig;

// prototype
public class MyEndpoint implements IEndpoint {

  private final ServiceProvider serviceProvider = new ServiceProvider() {
    @Override
    public Object getServiceByName(String serviceName) {
      return serverService;
    }
  };

  private IWSEndpointConfig config;

  private Object serverService;

  @Override
  public void init(final IWSEndpointConfig config) throws Exception {
    this.config = config;
    this.serverService = new MyServerService(
        newClientService(MyClientService.class) );
  }

  @Override
  public void onOpen(EndpointConfig config) throws Exception {
  }

  @Override
  public void onClose(CloseReason closeReason) throws Exception {
  }

  @Override
  public void onMessage(String message) throws Exception {

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
  }

  protected final RPCHandler createHandler() {
    return new DefaultRPCHandler(getServiceProvider() );
  }

  protected ServiceProvider getServiceProvider() {
    return serviceProvider;
  }

  @SuppressWarnings("unchecked")
  protected <C> C newClientService(
      final Class<C> clientServiceClass) throws Exception {

    return (C)Proxy.
      newProxyInstance(getClass().getClassLoader(),
      new Class[] { clientServiceClass },
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
  }
}
