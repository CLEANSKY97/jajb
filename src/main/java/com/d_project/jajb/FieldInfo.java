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

  public FieldInfo(
      final Class<?> target, final Field field) {
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
      getter = findGetter(target, nameSuffix);
      setter = target.getMethod("set" + nameSuffix,
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
      final Class<?> target, final String nameSuffix
      ) throws NoSuchMethodException {
    try {
      return target.getMethod("get" + nameSuffix);
    } catch(NoSuchMethodException e) {
      return target.getMethod("is" + nameSuffix);
    }
  }
}
