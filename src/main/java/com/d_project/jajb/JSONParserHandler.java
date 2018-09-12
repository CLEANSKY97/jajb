package com.d_project.jajb;


/**
 * JSONParserHandler
 * @author Kazuhiko Arase
 */
public interface JSONParserHandler {
  void beginArray() throws Exception;
  void endArray() throws Exception;
  void beginObject() throws Exception;
  void endObject() throws Exception;
  void onData(Object data) throws Exception;
}
