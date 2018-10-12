package com.d_project.jajb;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * DefaultJSONParserHandler
 * @author Kazuhiko Arase
 */
public class DefaultJSONParserHandler implements JSONParserHandler {

  private final Stack<StackData> stack = new Stack<StackData>();

  public DefaultJSONParserHandler() {
    this(null);
  }

  public DefaultJSONParserHandler(final Class<?> rootClass) {
    stack.push(new StackData(null, rootClass) );
  }

  @Override
  public void beginArray() throws IOException {
    stack.push(new StackData(
        new ArrayList<Object>(), stack.peek().targetClass) );
  }

  @Override
  public void endArray() throws IOException {
    onData(stack.pop().target);
  }

  protected Object getTargetObject(final Class<?> targetClass) {
    if (targetClass != null) {
      try {
        return targetClass.newInstance();
      } catch(RuntimeException e) { 
        throw e;
      } catch(Exception e) { 
        throw new RuntimeException(e);
      }
    } else {
      return new LinkedHashMap<String,Object>();
    }
  }

  protected Object[] getPath() {
    final Object[] path = new Object[stack.size()];
    for (int i = 0; i < path.length; i += 1) {
      final StackData objData = stack.get(i);
      path[i] = i == 0? "<root>" :
          objData.target instanceof Collection?
          objData.dataIndex : objData.name;
    }
    return path;
  }

  @Override
  public void beginObject() throws IOException {
    stack.push(new StackData(
        getTargetObject(stack.peek().targetClass), null) );
  }

  @Override
  public void endObject() throws IOException {
    onData(stack.pop().target);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void onData(final Object data) throws IOException {

    final StackData objData = stack.peek();
    final Object target = objData.target;

    if (target == null) {

      // root

    } else if (target.getClass().getAnnotation(JSONType.class) != null) {

      if (objData.dataIndex % 2 == 1) {
        setProperty(target, (String)objData.lastData, data);
      } else {
        objData.name = (String)data;
        final FieldInfo fieldInfo = MetadataCache.getMetadata(
            target.getClass() ).getFieldInfo(objData.name);
        objData.targetClass = fieldInfo == null? null :
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

  protected static Object castValue(
      final Class<?> clazz, final Object value) {

    if (value instanceof Number) {

      final Number dec = (Number)value;

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

      } else if (BigInteger.class.isAssignableFrom(clazz) ) {
        return BigInteger.valueOf(dec.longValue() );

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
      final String name, final Object value) {

    final FieldInfo fieldInfo =
        MetadataCache.getMetadata(target.getClass() ).
        getFieldInfo(name);
    if (fieldInfo == null) {
      return;
    }

    fieldInfo.set(target, castValue(fieldInfo.getType(), value) );
  }

  public Object getLastData() {
    final StackData lastStack = stack.peek();
    return lastStack.targetClass != null?
        castValue(lastStack.targetClass, lastStack.lastData) :
          lastStack.lastData;
  }

  public Object getStackObject(int depth) {
    return stack.get(depth).target;
  }

  protected static class StackData {
    public final Object target;
    public Class<?> targetClass;
    public String name = null;
    public Object lastData = null;
    public int dataIndex = 0;
    public StackData(Object target, Class<?> targetClass) {
      this.target = target;
      this.targetClass = targetClass;
    }
  }

}
