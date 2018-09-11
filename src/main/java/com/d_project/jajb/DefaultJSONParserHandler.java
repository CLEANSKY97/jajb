package com.d_project.jajb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

/**
 * DefaultJSONParserHandler
 * @author Kazuhiko Arase
 */
public class DefaultJSONParserHandler implements JSONParserHandler {

  protected final Stack<StackData> stack = new Stack<>();

  public DefaultJSONParserHandler() {
    stack.push(new StackData(null) );
  }

  @Override
  public void beginArray() throws IOException {
    stack.push(new StackData(new ArrayList<>() ) );
  }

  @Override
  public void endArray() throws IOException {
    onData(stack.pop().target);
  }

  @Override
  public void beginObject() throws IOException {
    stack.push(new StackData(new LinkedHashMap<>() ) );
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
    } else if (target instanceof Map) {
      if (objData.count % 2 == 1) {
        ((Map<Object,Object>)target).put(objData.lastData, data);
      }
    } else if (target instanceof Collection) {
      ((Collection<Object>)target).add(data);
    }

    objData.lastData = data;
    objData.count += 1;
  }

  public Object getLastData() {
    return stack.peek().lastData;
  }

  protected static class StackData {
    public final Object target;
    public Object lastData = null;
    public int count = 0;
    public StackData(Object target) {
      this.target = target;
    }
  }
}
