package com.d_project.jajb;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.d_project.jajb.Metadata.FieldInfo;

/**
 * DefaultJSONParserHandler
 * @author Kazuhiko Arase
 */
public class DefaultJSONParserHandler implements JSONParserHandler {

  protected final Stack<StackData> stack = new Stack<StackData>();

  private Class<?> targetClass;

  public DefaultJSONParserHandler() {
    this(null);
  }

  public DefaultJSONParserHandler(final Class<?> targetClass) {
    stack.push(new StackData(null) );
    this.targetClass = targetClass;
  }

  @Override
  public void beginArray() throws Exception {
    stack.push(new StackData(new ArrayList<Object>() ) );
  }

  @Override
  public void endArray() throws Exception {
    onData(stack.pop().target);
  }

  protected Object getTargetObject(
      Class<?> targetClass, Object[] path) throws Exception {
    System.out.println(Arrays.asList(path) );
    if (targetClass != null) {
      return targetClass.newInstance();
    } else {
      return new LinkedHashMap<String,Object>();
    }
  }

  @Override
  public void beginObject() throws Exception {
    final Object[] path = new Object[stack.size() - 1];
    for (int i = 0; i < path.length; i += 1) {
      final StackData objData = stack.get(i + 1);
      path[i] = objData.target instanceof Collection?
          objData.dataIndex : objData.name;
    }
    stack.push(new StackData(getTargetObject(targetClass, path) ) );
  }

  @Override
  public void endObject() throws Exception {
    onData(stack.pop().target);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void onData(final Object data) throws Exception {

    final StackData objData = stack.peek();
    final Object target = objData.target;

    if (target == null) {

      // root

    } else if (target.getClass().
        getAnnotation(JSONSerializable.class) != null) {

      if (objData.dataIndex % 2 == 1) {
        setProperty(target, (String)objData.lastData, data);
      } else {
        objData.name = (String)data;
        final FieldInfo fieldInfo = MetadataCache.getMetadata(
            target.getClass() ).getFieldInfo(objData.name);
        targetClass = fieldInfo == null? null :
            fieldInfo.getIterableType() != null?
            fieldInfo.getIterableType() : fieldInfo.getType();
      }

    } else if (target instanceof Map) {

      if (objData.dataIndex % 2 == 1) {
        ((Map<Object,Object>)target).put(objData.lastData, data);
      } else {
        objData.name = (String)data;
      }

    } else if (target instanceof Collection) {
      ((Collection<Object>)target).add(data);
    }

    objData.lastData = data;
    objData.dataIndex += 1;
  }

  protected Object castValue(
      final Class<?> clazz, final Object value) {
    if (value instanceof BigDecimal) {

      final BigDecimal dec = (BigDecimal)value;

      if (clazz.equals(Integer.TYPE) ) {
        return dec.intValue();
      } else if (clazz.equals(Integer.class) ) {
        return dec.intValue();

      } else if (clazz.equals(Long.TYPE) ) {
        return dec.longValue();
      } else if (clazz.equals(Long.class) ) {
        return dec.longValue();

      } else if (clazz.equals(Byte.TYPE) ) {
        return dec.byteValue();
      } else if (clazz.equals(Byte.class) ) {
        return dec.byteValue();

      } else if (clazz.equals(Short.TYPE) ) {
        return dec.shortValue();
      } else if (clazz.equals(Short.class) ) {
        return dec.shortValue();

      } else if (clazz.equals(Float.TYPE) ) {
        return dec.floatValue();
      } else if (clazz.equals(Float.class) ) {
        return dec.floatValue();

      } else if (clazz.equals(Double.TYPE) ) {
        return dec.doubleValue();
      } else if (clazz.equals(Double.class) ) {
        return dec.doubleValue();

      } else {
        return value;
      }

    } else if (clazz.isArray() ) {

      final List<?> list = (List<?>)value;
      final int len = list.size();
      final Class<?> ctype = clazz.getComponentType();
      final Object array = Array.newInstance(ctype, len);
      for (int i = 0; i < len; i += 1) {
        Array.set(array, i, castValue(ctype, list.get(i) ) );
      }
      return array;

    } else {
      return value;
    }
  }

  protected void setProperty(final Object target,
      final String name, final Object value) throws Exception {

    final FieldInfo fieldInfo =
        MetadataCache.getMetadata(target.getClass() ).
        getFieldInfo(name);
    if (fieldInfo == null) {
      return;
    }

    fieldInfo.set(target, castValue(fieldInfo.getType(), value) );
  }

  public Object getLastData() {
    return stack.peek().lastData;
  }

  protected static class StackData {
    public final Object target;
    public String name = null;
    public Object lastData = null;
    public int dataIndex = 0;
    public StackData(Object target) {
      this.target = target;
    }
  }

}
