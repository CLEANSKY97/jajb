package com.d_project.jajb.rpc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ObjectUtil
 * @author Kazuhiko Arase
 */
public class ObjectUtil {
  private ObjectUtil() {
  }
  public static List<Object> asList(Object... args) {
    return Arrays.asList(args);
  }
  public static Map<String,Object> asMap(Object... args) {
    Map<String,Object> map = new HashMap<String, Object>();
    if (args.length % 2 != 0) {
      throw new RuntimeException();
    }
    for (int i = 0; i < args.length; i += 2) {
      map.put( (String)args[i], args[i + 1]);
    }
    return map;
  }
}
