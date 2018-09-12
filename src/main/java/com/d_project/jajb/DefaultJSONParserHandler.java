package com.d_project.jajb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import com.d_project.jajb.Metadata.FieldInfo;

/**
 * DefaultJSONParserHandler
 * @author Kazuhiko Arase
 */
public class DefaultJSONParserHandler implements JSONParserHandler {

  protected final Stack<StackData> stack = new Stack<StackData>();

  protected Class<?> targetClass;

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

  @Override
  public void beginObject() throws Exception {
    if (this.targetClass != null) {
      stack.push(new StackData(targetClass.newInstance() ) );
    } else {
      stack.push(new StackData(new LinkedHashMap<String,Object>() ) );
    }
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
        final String name = (String)data;
        final FieldInfo fieldInfo = MetadataCache.getMetadata(
            target.getClass() ).getFieldInfo(name);
        targetClass = fieldInfo == null? null :
            fieldInfo.getIterableType() != null?
            fieldInfo.getIterableType() : fieldInfo.getType();
      }

    } else if (target instanceof Map) {

      if (objData.dataIndex % 2 == 0) {
        targetClass = null;
      } else if (objData.dataIndex % 2 == 1) {
        ((Map<Object,Object>)target).put(objData.lastData, data);
      }

    } else if (target instanceof Collection) {
      targetClass = null;
      ((Collection<Object>)target).add(data);
    }

    objData.lastData = data;
    objData.dataIndex += 1;
  }

  protected void setProperty(final Object target,
      final String name, final Object value) throws Exception {

    final FieldInfo fieldInfo =
        MetadataCache.getMetadata(target.getClass() ).
        getFieldInfo(name);
    if (fieldInfo == null) {
      return;
    }

    final Class<?> fieldType = fieldInfo.getType();

    if (value instanceof BigDecimal) {

      final BigDecimal dec = (BigDecimal)value;

      if (fieldType.equals(Integer.TYPE) ) {
        fieldInfo.set(target, dec.intValue() );
      } else if (fieldType.equals(Integer.class) ) {
        fieldInfo.set(target, dec.intValue() );

      } else if (fieldType.equals(Long.TYPE) ) {
        fieldInfo.set(target, dec.longValue() );
      } else if (fieldType.equals(Long.class) ) {
        fieldInfo.set(target, dec.longValue() );

      } else if (fieldType.equals(Byte.TYPE) ) {
        fieldInfo.set(target, dec.byteValue() );
      } else if (fieldType.equals(Byte.class) ) {
        fieldInfo.set(target, dec.byteValue() );

      } else if (fieldType.equals(Short.TYPE) ) {
        fieldInfo.set(target, dec.shortValue() );
      } else if (fieldType.equals(Short.class) ) {
        fieldInfo.set(target, dec.shortValue() );

      } else if (fieldType.equals(Float.TYPE) ) {
        fieldInfo.set(target, dec.floatValue() );
      } else if (fieldType.equals(Float.class) ) {
        fieldInfo.set(target, dec.floatValue() );

      } else if (fieldType.equals(Double.TYPE) ) {
        fieldInfo.set(target, dec.doubleValue() );
      } else if (fieldType.equals(Double.class) ) {
        fieldInfo.set(target, dec.doubleValue() );

      } else {
        fieldInfo.set(target, value);
      }

    } else {
      fieldInfo.set(target, value);
    }
  }

  public Object getLastData() {
    return stack.peek().lastData;
  }

  protected static class StackData {
    public final Object target;
    public Object lastData = null;
    public int dataIndex = 0;
    public StackData(Object target) {
      this.target = target;
    }
  }

}
