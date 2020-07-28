package com.wurmcraft.serveressentials.forge.server.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import org.reflections.Reflections;

public class AnnotationUtils {

  private static final Reflections REFLECTIONS = new Reflections(
      "com.wurmcraft.serveressentials.forge");

  public static Set<Class<?>> findAnnotation(Class<? extends Annotation> annotation) {
    return REFLECTIONS.getTypesAnnotatedWith(annotation);
  }

  public static Set<Method> findAnnotationMethods(Class<?> clazz,
      Class<? extends Annotation> annotation) {
    Set<Method> annotatedMethods = new HashSet<>();
    for (Method method : clazz.getDeclaredMethods()) {
      if (method.isAnnotationPresent(annotation)) {
        annotatedMethods.add(method);
      }
    }
    return annotatedMethods;
  }
}
