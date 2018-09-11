package com.d_project.jajb;

import java.io.IOException;

/**
 * JSONParserHandler
 * @author Kazuhiko Arase
 */
public interface JSONParserHandler {
  void beginArray() throws IOException;
  void endArray() throws IOException;
  void beginObject() throws IOException;
  void endObject() throws IOException;
  void onData(Object data) throws IOException;
}
