package com.d_project.jajb.rpc;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.d_project.jajb.DefaultJSONParserHandler;
import com.d_project.jajb.JSONSerializable;

/**
 * RPCHandler
 * @author Kazuhiko Arase
 */
public class RPCHandler extends DefaultJSONParserHandler {

  protected static final Integer ARG_INDEX = Integer.valueOf(1);
  protected static final int PARAMS_DEPTH = 1;
  protected static final int ARGS_DEPTH = PARAMS_DEPTH + 1;

  private Object service = null;
  private Method targetMethod = null;

  public RPCHandler() {
    super();
  }

  @SuppressWarnings("unchecked")
  public Object call() throws Exception {
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
      final List<Object> args = (List<Object>)params.
          get(ARG_INDEX.intValue() );
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
      final Class<?> paramType = targetMethod.
          getParameterTypes()[paramIndex];
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

  private static final ConcurrentHashMap<String, Method>
      targetMethodCache = new ConcurrentHashMap<String, Method>();

  protected static Method findTargetMethod(
      final Object obj, final String methodName) {
    final String key = obj.getClass().getName() + "#" + methodName;
    Method targetMethod = targetMethodCache.get(key);
    if (targetMethod == null) {
      targetMethod = findTargetMethodImpl(obj, methodName);
      targetMethodCache.putIfAbsent(key, targetMethod);
    }
    return targetMethod;
  }

  private static Method findTargetMethodImpl(
      final Object obj, final String methodName) {
    for (Class<?> clazz = obj.getClass();
        clazz != null; clazz = clazz.getSuperclass() ) {
      Method targetMethod = null;
      for (final Method method : clazz.getDeclaredMethods() ) {
        if (method.getName().equals(methodName) ) {
          if (method.getAnnotation(Callable.class) != null) {
            if (targetMethod != null) {
              throw new RuntimeException("method duplicated : " + methodName);
            }
            targetMethod = method;
          }
        }
      }
      if (targetMethod != null) {
        return targetMethod;
      }
    }
    throw new RuntimeException("method not found : " + methodName);
  }
}
