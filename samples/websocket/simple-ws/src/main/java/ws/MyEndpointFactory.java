package ws;

import java.util.Map;
import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;

import com.d_project.jajb.rpc.ws.IEndpoint;
import com.d_project.jajb.rpc.ws.IEndpointFactory;

public class MyEndpointFactory implements IEndpointFactory {

  private static final Logger logger =
      Logger.getLogger(MyEndpointFactory.class.getName() );

  @Override
  public IEndpoint createEndpoint(
      final Map<String, Object> endpointConfig) {
    p
    return new IEndpoint() {
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
      }
    };
  }
}
