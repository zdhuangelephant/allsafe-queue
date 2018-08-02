package com.allsafe.queue.util;

import java.util.Set;

import com.google.common.collect.Sets;

public class QueueUtil {
  public static Set<Class<?>> getSuperSet(Class<?> clazz) {
    Set<Class<?>> classSet = Sets.newHashSet();
    classSet.add(clazz);
    for (Class<?> iterface : clazz.getInterfaces()) {
      classSet.addAll(getSuperSet(iterface));
    }
    if (clazz.equals(Object.class) || null == clazz.getSuperclass()
        || clazz.getSuperclass().equals(Object.class)) return classSet;
    classSet.addAll(getSuperSet(clazz.getSuperclass()));
    return classSet;
  }

}
