package com.d_project.jajb.rpc;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

/**
 * DOMResultHandler
 * @author Kazuhiko Arase
 */
public class DOMResultHandler implements ResultHandler {
  private final Node node;
  public DOMResultHandler(final Node node) {
    this.node = node;
  }
  public void handle(
    final HttpServletResponse response
  ) throws ServletException, IOException {
    response.setContentType("application/xml;charset=UTF-8");
    final OutputStream out = new BufferedOutputStream(
        response.getOutputStream() );
    try {
      final Transformer tf = TransformerFactory.
          newInstance().newTransformer();
      tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      tf.transform(
          new DOMSource(node),
          new StreamResult(out) );
    } catch(TransformerException e) {
      throw new IOException(e);
    } finally {
      out.close();
    }
  }
}
