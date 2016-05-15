/*
 * Copyright (C) 2016 Christian Stein
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.sormuras.listing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * An annotation object denotes a specific invocation of an annotation type.
 *
 * @author Christian Stein
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.7
 */
public class JavaAnnotation implements Listable {

  public static JavaAnnotation of(Annotation annotation) {
    return of(annotation, false);
  }

  public static List<JavaAnnotation> of(Annotation... annotations) {
    return Arrays.asList(annotations).stream().map(JavaAnnotation::of).collect(Collectors.toList());
  }

  public static JavaAnnotation of(Annotation annotation, boolean includeDefaultValues) {
    JavaAnnotation object = of(annotation.annotationType());
    try {
      Method[] methods = annotation.annotationType().getDeclaredMethods();
      Arrays.sort(methods, (m1, m2) -> m1.getName().compareTo(m2.getName()));
      for (Method method : methods) {
        Object value = method.invoke(annotation);
        if (!includeDefaultValues) {
          if (Objects.deepEquals(value, method.getDefaultValue())) {
            continue;
          }
        }
        if (value.getClass().isArray()) {
          for (int i = 0; i < Array.getLength(value); i++) {
            object.addObject(method.getName(), Array.get(value, i));
          }
          continue;
        }
        if (value instanceof Annotation) {
          object.addMember(method.getName(), of((Annotation) value));
          continue;
        }
        object.addObject(method.getName(), value);
      }
    } catch (Exception e) {
      throw new AssertionError("reflecting " + annotation + " failed: " + e.getCause(), e);
    }
    return object;
  }

  public static JavaAnnotation of(Class<? extends Annotation> type, Object... values) {
    return new JavaAnnotation(JavaName.of(type), values);
  }

  private final Map<String, List<Listable>> members;
  private final JavaName typeName;

  public JavaAnnotation(JavaName typeName, Object... values) {
    this.typeName = Tool.check(typeName, "typeName");
    this.members = new LinkedHashMap<>();
    Arrays.asList(values).forEach(this::addValue);
  }

  public JavaAnnotation addMember(String name, Listable listable) {
    Tool.check(name, "name");
    Tool.check(listable, "listable");
    List<Listable> values = members.get(name);
    if (values == null) {
      values = new ArrayList<>();
      members.put(name, values);
    }
    values.add(listable);
    return this;
  }

  public JavaAnnotation addObject(String memberName, Object object) {
    Tool.check(object, "constant non-null object expected as value for " + memberName);
    return addMember(memberName, Listable.of(object));
  }

  public JavaAnnotation addValue(Object object) {
    return addObject("value", object);
  }

  @Override
  public Listing apply(Listing listing) {
    // always emit "@" and the typename
    listing.add('@').add(getTypeName());
    // trivival case: marker annotation w/o members
    if (members.isEmpty()) {
      return listing;
    }
    // simple case: single element annotation w/ member called "value"
    if (members.size() == 1 && members.containsKey("value")) {
      return listing.add('(').add(values(members.get("value"))).add(')');
    }
    // normal annotation: emit all "key = value" pairs
    Consumer<Entry<String, List<Listable>>> separate = entry -> listing.add(", ");
    Consumer<Entry<String, List<Listable>>> print =
        e -> listing.add(e.getKey()).add(" = ").add(values(e.getValue()));
    Spliterator<Entry<String, List<Listable>>> entries = members.entrySet().spliterator();
    listing.add('(');
    entries.tryAdvance(print);
    entries.forEachRemaining(separate.andThen(print));
    listing.add(')');
    return listing;
  }

  public Map<String, List<Listable>> getMembers() {
    return members;
  }

  public JavaName getTypeName() {
    return typeName;
  }

  @Override
  public String toString() {
    return "JavaAnnotation{" + getTypeName() + ", members=" + getMembers() + "}";
  }

  /** Annotation array-aware value(s) appender. */
  private Listable values(List<Listable> values) {
    return (listing) -> {
      if (values.size() == 1) {
        return listing.add(values.get(0));
      }
      return listing.add('{').add(values, ", ").add('}');
    };
  }
}
