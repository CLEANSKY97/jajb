package com.d_project.jajb.rpc;

import java.lang.reflect.Method;
import java.util.Map;

import com.d_project.jajb.JSONParserHandler;

/**
 * RPCHandler
 * @author Kazuhiko Arase
 */
public interface RPCHandler extends JSONParserHandler {
  Map<String,Object> getOpts();
  Method getTargetMethod() throws Exception;
  Object call() throws Exception;
}
