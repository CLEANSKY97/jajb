package com.d_project.jajb.rpc;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
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
    final HttpServletRequest request,
    final HttpServletResponse response
  ) throws ServletException, IOException {
    final String encoding = "UTF-8";
    response.setContentType("application/xml;charset=" + encoding);
    GZIPUtil.output(request, response, new GZIPUtil.OutputHandler() {
      @Override
      public void writeTo(final OutputStream out) throws IOException {
        try {
          final Transformer tf = TransformerFactory.
              newInstance().newTransformer();
          tf.setOutputProperty(OutputKeys.ENCODING, encoding);
          tf.transform(
              new DOMSource(node),
              new StreamResult(out) );
        } catch(TransformerException e) {
          throw new IOException(e);
        } finally {
          out.flush();
        }
      }
    });
  }
}
