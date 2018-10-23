package com.d_project.jajb.rpc;

import java.lang.reflect.Method;

import com.d_project.jajb.JSONParserHandler;

/**
 * RPCHandler
 * @author Kazuhiko Arase
 */
public interface RPCHandler extends JSONParserHandler {
  Method getTargetMethod() throws Exception;
  Object call() throws Exception;
  Object getLastData();
}
