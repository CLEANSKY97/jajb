package com.d_project.jajb;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Metadata
 * @author Kazuhiko Arase
 */
class Metadata {

  private static final Comparator<FieldInfo> fieldInfoComparator =
      new Comparator<FieldInfo>() {
    @Override
    public int compare(final FieldInfo f1, final FieldInfo f2) {
      final int cp = f1.getOrder() - f2.getOrder();
      if (cp != 0) {
        return cp;
      }
      // default: sort by name
      return f1.getName().compareTo(f2.getName() );
    }
  };

  private final Map<String,FieldInfo> fieldInfoMap;

  public Metadata(final Class<?> target) {

    fieldInfoMap = new LinkedHashMap<String, FieldInfo>();

    // check for duplicated field name.
    final Set<String> fieldNames = new HashSet<String>();

    for (Class<?> clazz = target;
        clazz != null; clazz = clazz.getSuperclass() ) {

      if (clazz.getAnnotation(JSONType.class) == null) {
        continue;
      }

      final List<FieldInfo> fieldInfoList = new ArrayList<FieldInfo>();
      for (final Field field : clazz.getDeclaredFields() ) {
        if (fieldNames.contains(field.getName() ) ) {
          throw new IllegalStateException("duplicated field:" + field);
        }
        fieldNames.add(field.getName() );
        final JSONField fieldAnnot = field.getAnnotation(JSONField.class);
        if (fieldAnnot != null) {
          fieldInfoList.add(new FieldInfo(clazz,
              field, fieldAnnot.order() ) );
        }
      }

      // sort fields order.
      Collections.sort(fieldInfoList, fieldInfoComparator);

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

  public Collection<FieldInfo> getFieldInfoList() {
    return fieldInfoMap.values();
  }
}
