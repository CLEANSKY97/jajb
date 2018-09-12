package com.d_project.jajb;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Metadata
 * @author Kazuhiko Arase
 */
class Metadata {

  private final Map<String,FieldInfo> fieldInfoMap;

  public Metadata(final Class<?> target) throws Exception {

    fieldInfoMap = new LinkedHashMap<String, FieldInfo>();

    for (Class<?> clazz = target;
        clazz != null; clazz = clazz.getSuperclass() ) {

      final List<FieldInfo> fieldInfoList = new ArrayList<FieldInfo>();
      for (Field field : clazz.getDeclaredFields() ) {
        if (field.getAnnotation(JSONSerializable.class) != null) {
          fieldInfoList.add(new FieldInfo(clazz, field) );
        }
      }

      // fix fields order.
      fieldInfoList.sort(new Comparator<FieldInfo>() {
        @Override
        public int compare(FieldInfo f1, FieldInfo f2) {
          return f1.getName().compareTo(f2.getName() );
        }
      });

      for (FieldInfo fieldInfo : fieldInfoList) {
        if (!fieldInfoMap.containsKey(fieldInfo.getName() ) ) {
          fieldInfoMap.put(fieldInfo.getName(), fieldInfo);
        }
      }
    }
  }

  public FieldInfo getFieldInfo(final String name) {
    return fieldInfoMap.get(name);
  }
  public Iterable<FieldInfo> getFieldInfoList() {
    return fieldInfoMap.values();
  }

  public static class FieldInfo {

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
      } catch(RuntimeException e) { 
        throw e;
      } catch(Exception e) { 
        throw new RuntimeException(e);
      }
      
      return getter.invoke(target);
    }
    public void set(final Object target,
        final Object value) throws NoSuchMethodException {
      try {
      } catch(RuntimeException e) { 
        throw e;
      } catch(Exception e) { 
        throw new RuntimeException(e);
      }
      setter.invoke(target, value);
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
}
