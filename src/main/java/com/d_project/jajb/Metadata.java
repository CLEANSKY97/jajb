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

  public Metadata(final Class<?> target) {

    fieldInfoMap = new LinkedHashMap<String, FieldInfo>();

    for (Class<?> clazz = target;
        clazz != null; clazz = clazz.getSuperclass() ) {

      final List<FieldInfo> fieldInfoList = new ArrayList<FieldInfo>();
      for (final Field field : clazz.getDeclaredFields() ) {
        if (field.getAnnotation(JSONSerializable.class) != null) {
          fieldInfoList.add(new FieldInfo(clazz, field) );
        }
      }

      // fix fields order.
      fieldInfoList.sort(new Comparator<FieldInfo>() {
        @Override
        public int compare(final FieldInfo f1, final FieldInfo f2) {
          return f1.getName().compareTo(f2.getName() );
        }
      });

      for (final FieldInfo fieldInfo : fieldInfoList) {
        if (!fieldInfoMap.containsKey(fieldInfo.getName() ) ) {
          fieldInfoMap.put(fieldInfo.getName(), fieldInfo);
        }
      }
    }
  }

  public FieldInfo getFieldInfo(final String name) {
    return fieldInfoMap.get(name);
  }
  public Iterable<FieldInfo> getFieldInfos() {
    return fieldInfoMap.values();
  }
}
