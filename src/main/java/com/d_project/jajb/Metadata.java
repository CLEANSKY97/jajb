package com.d_project.jajb;

import java.lang.reflect.Field;
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

    final List<FieldInfo> fieldInfoList = new ArrayList<FieldInfo>();
    for (Field field : target.getDeclaredFields() ) {
      if (field.getAnnotation(JSONSerializable.class) != null) {
        field.setAccessible(true);
        fieldInfoList.add(new FieldInfo(field) );
      }
    }

    // fix fields order.
    fieldInfoList.sort(new Comparator<FieldInfo>() {
      @Override
      public int compare(FieldInfo f1, FieldInfo f2) {
        return f1.getField().getName().compareTo(f2.getField().getName() );
      }
    });

    fieldInfoMap = new LinkedHashMap<String, FieldInfo>();
    for (FieldInfo fieldInfo : fieldInfoList) {
      fieldInfoMap.put(fieldInfo.getField().getName(), fieldInfo);
    }
  }

  public FieldInfo getFieldInfo(final String name) {
    return fieldInfoMap.get(name);
  }
  public Iterable<FieldInfo> getFieldInfoList() {
    return fieldInfoMap.values();
  }

  public static class FieldInfo {
    private final Field field;
    public FieldInfo(final Field field) {
      this.field = field;
    }
    public Field getField() {
      return field;
    }
  }
}
