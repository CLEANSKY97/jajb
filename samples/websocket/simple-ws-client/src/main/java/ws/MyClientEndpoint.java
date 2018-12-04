package ws;

import java.util.logging.Logger;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

@ClientEndpoint
public class MyClientEndpoint {

  protected static final Logger logger =
      Logger.getLogger(MyClientEndpoint.class.getName() );

  private static final Object closeLock = new Object();

  public static void waitForClose() throws Exception {
    synchronized(closeLock) {
      closeLock.wait();
    }
  }

  public MyClientEndpoint() {
  }

  @OnOpen
  public void onOpen(Session session) throws Exception {
  }

  @OnClose
  public void onClose(Session session, CloseReason reason) {
    synchronized(closeLock) {
      closeLock.notify();
    }
  }

  @OnMessage
  public void onMessage(String message, Session session) {
  }

  @OnError
  public void onError(Throwable t) {
  }
}
