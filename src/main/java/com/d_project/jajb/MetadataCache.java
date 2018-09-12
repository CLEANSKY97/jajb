package com.d_project.jajb;

import java.util.concurrent.ConcurrentHashMap;

/**
 * MetadataCache
 * @author Kazuhiko Arase
 */
class MetadataCache {

  private static ConcurrentHashMap<Class<?>, Metadata> cache =
      new ConcurrentHashMap<Class<?>, Metadata>();

  public static Metadata getMetadata(Class<?> clazz) throws Exception {
    Metadata metadata = cache.get(clazz);
    if (metadata == null) {
      metadata = new Metadata(clazz);
      cache.putIfAbsent(clazz, metadata);
    }
    return metadata;
  }

  private MetadataCache() {
  }
}
