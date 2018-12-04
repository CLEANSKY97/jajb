package ws;

import java.awt.Desktop;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
import java.util.logging.Logger;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

public class Main {

  protected static final Logger logger =
      Logger.getLogger(Main.class.getName() );

  public static void main(String[] args) throws Exception {
    if (System.getProperty("java.net.useSystemProxies") == null) {
      System.setProperty("java.net.useSystemProxies", "true");
    }
    new Main().start();
  }

  protected Properties loadConfig() throws Exception {
    Properties config = new Properties();
    InputStream in = getClass().getResourceAsStream("/config.properties");
    try {
      config.load(in);
    } finally {
      in.close();
    }
    return config;
  }

  protected void start() throws Exception {

    final Properties config = loadConfig();
    final String baseUrl = config.getProperty("baseUrl");
    final String wsUrl = baseUrl.replaceAll("^http", "ws") + "/my-ws";
    final String indexUrl = baseUrl + "/index.html";

    Desktop.getDesktop().browse(new URI(indexUrl) );

    WebSocketContainer container =
        ContainerProvider.getWebSocketContainer();

    while (true) {
      try {
        container.connectToServer(
            MyClientEndpoint.class, URI.create(wsUrl) );
        MyClientEndpoint.waitForClose();
      } catch(Exception e) {
        Thread.sleep(5000);
      }
    }
  }
}
