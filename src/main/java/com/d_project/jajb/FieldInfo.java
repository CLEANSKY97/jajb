package com.d_project.jajb;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FieldInfo
 * @author Kazuhiko Arase
 */
public class FieldInfo {

  protected static final Logger logger =
      Logger.getLogger(FieldInfo.class.getName() );

  private final String name;
  private final int order;
  private final Class<?> type;
  private final Class<?> iterableType;
  private final Method getter;
  private final Method setter;

  public FieldInfo(final Class<?> clazz,
      final Field field, final int order) {
    try {

      this.name = field.getName();
      this.type = field.getType();
      this.order = order;

      if (Iterable.class.isAssignableFrom(type) ) {
        final ParameterizedType pt =
            (ParameterizedType)field.getGenericType();
        final Type it = pt.getActualTypeArguments()[0];
        if (it instanceof Class) {
          iterableType = (Class<?>)it;
        } else {
          iterableType = Class.forName(it.toString() );
        }
      } else {
        iterableType = null;
      }

      final String nameSuffix = name.substring(0, 1).toUpperCase() +
          name.substring(1);
      getter = findGetter(clazz, nameSuffix, field.getType() );
      setter = clazz.getMethod("set" + nameSuffix,
          new Class[] { field.getType() });
    } catch(RuntimeException e) { 
      throw e;
    } catch(Exception e) { 
      throw new RuntimeException(e);
    }
  }
  public String getName() {
    return name;
  }
  public Class<?> getType() {
    return type;
  }
  public int getOrder() {
    return order;
  }
  public Class<?> getIterableType() {
    return iterableType;
  }
  public Object get(final Object target) {
    try {
      return getter.invoke(target);
    } catch(RuntimeException e) { 
      throw e;
    } catch(Exception e) { 
      throw new RuntimeException(e);
    }
  }
  public void set(final Object target,
      final Object value) {
    try {
      setter.invoke(target, value);
    } catch(IllegalArgumentException e) { 
      logger.log(Level.FINEST, setter + ",value=" + value, e);
      throw e;
    } catch(RuntimeException e) { 
      throw e;
    } catch(Exception e) { 
      throw new RuntimeException(e);
    }
  }

  protected static Method findGetter(
      final Class<?> clazz,
      final String nameSuffix,
      final Class<?> type) throws NoSuchMethodException {
    if (Boolean.TYPE.equals(type) || Boolean.class.equals(type) ) {
      try {
        return clazz.getMethod("is" + nameSuffix);
      } catch(NoSuchMethodException e) {
        return clazz.getMethod("get" + nameSuffix);
      }
    } else {
      return clazz.getMethod("get" + nameSuffix);
    }
  }
}
