package com.d_project.jajb.rpc;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.d_project.jajb.DefaultJSONParserHandler;
import com.d_project.jajb.JSONSerializable;

/**
 * RPCRequestParserHandler
 * @author Kazuhiko Arase
 */
public class RPCInvokerHandler extends DefaultJSONParserHandler {

  protected static final Integer ARG_INDEX = Integer.valueOf(1);
  protected static final int PARAMS_DEPTH = 1;
  protected static final int ARGS_DEPTH = PARAMS_DEPTH + 1;

  private Object service = null;
  private Method targetMethod = null;

  @SuppressWarnings("unchecked")
  public Object invoke() throws Exception {
    final List<Object> params = (List<Object>)getLastData();
    final List<Object> args = (List<Object>)params.get(ARG_INDEX);
    return targetMethod.invoke(service, args.toArray() );
  }

  @Override
  @SuppressWarnings("unchecked")
  public void endArray() throws IOException {
    super.endArray();
    if (getPath().length == PARAMS_DEPTH) {
      final List<Object> params = (List<Object>)getLastData();
      final List<Object> args =  (List<Object>)params.get(ARG_INDEX.intValue() );
      for (int i = 0; i < args.size(); i += 1) {
        final Class<?> clazz = targetMethod.getParameterTypes()[i];
        args.set(i, castValue(clazz, args.get(i) ) );
      }
    }
  }

  @SuppressWarnings("unchecked")
  protected Method getTargetMethod() {
    if (service == null) {
      final List<Object> params = (List<Object>)getStackObject(PARAMS_DEPTH);
      final Map<String,Object> opts = (Map<String,Object>)params.get(0);
      service = ServiceLocator.getInstance().
          getService( (String)opts.get("serviceName") );
      targetMethod = findTargetMethod(
          service, (String)opts.get("methodName") );
    }
    return targetMethod;
  }

  @Override
  protected Object getTargetObject(final Class<?> targetClass) {
    final Object[] path = getPath();
    if (path.length - 1 == ARGS_DEPTH && ARG_INDEX.equals(path[1]) ) {
      final Method targetMethod = getTargetMethod();
      final int paramIndex = ( (Integer)path[ARGS_DEPTH]).intValue();
      final Class<?> paramType =
          targetMethod.getParameterTypes()[paramIndex];
      if (paramType.getAnnotation(JSONSerializable.class) != null) {
        try {
          return paramType.newInstance();
        } catch(RuntimeException e) { 
          throw e;
        } catch(Exception e) { 
          throw new RuntimeException(e);
        }
      }
    }

    return super.getTargetObject(targetClass);
  }

  protected Method findTargetMethod(Object obj, String methodName) {
    // TODO en-cache
    for (Class<?> clazz = obj.getClass();
        clazz != null; clazz = clazz.getSuperclass() ) {
      Method targetMethod = null;
      for (final Method method : clazz.getDeclaredMethods() ) {
        if (method.getName().equals(methodName) ) {
          if (targetMethod != null) {
            // TODO
            throw new RuntimeException("dup");
          }
          targetMethod = method;
        }
      }
      if (targetMethod != null) {
        return targetMethod;
      }
    }
    throw new RuntimeException("method not found : " + methodName);
  }
}
