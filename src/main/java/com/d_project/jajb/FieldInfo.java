package com.d_project.jajb;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

/**
 * FieldInfo
 * @author Kazuhiko Arase
 */
public class FieldInfo {

  private final String name;
  private final Class<?> type;
  private final Class<?> iterableType;
  private final Method getter;
  private final Method setter;

  public FieldInfo(final Class<?> clazz, final Field field) {
    try {

      this.name = field.getName();
      this.type = field.getType();

      if (Iterable.class.isAssignableFrom(type) ) {
        final ParameterizedType pt =
            (ParameterizedType)field.getGenericType();
        iterableType = Class.forName(
            pt.getActualTypeArguments()[0].getTypeName() );
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
