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

import static com.github.sormuras.listing.Tool.escape;
import static java.util.Objects.requireNonNull;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * An annotation object denotes a specific invocation of an annotation type.
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.7
 */
public class Annotation implements Listable {

  public static Annotation of(java.lang.annotation.Annotation annotation) {
    return of(annotation, false);
  }

  public static List<Annotation> of(java.lang.annotation.Annotation... annotations) {
    return Arrays.asList(annotations).stream().map(Annotation::of).collect(Collectors.toList());
  }

  public static Annotation of(
      java.lang.annotation.Annotation annotation, boolean includeDefaultValues) {
    Annotation result = of(annotation.annotationType());
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
            result.addObject(method.getName(), Array.get(value, i));
          }
          continue;
        }
        if (value instanceof java.lang.annotation.Annotation) {
          result.addMember(method.getName(), of((java.lang.annotation.Annotation) value));
          continue;
        }
        result.addObject(method.getName(), value);
      }
    } catch (Exception e) {
      throw new AssertionError("reflecting " + annotation + " failed: " + e.getCause(), e);
    }
    return result;
  }

  public static Annotation of(
      Class<? extends java.lang.annotation.Annotation> type, Object... values) {
    return new Annotation(Name.of(type), values);
  }

  public static Listable value(Object o) {
    if (o instanceof Class) return listing -> listing.add(Name.of((Class<?>) o)).add(".class");
    if (o instanceof Enum) return listing -> listing.add(Name.of((Enum<?>) o));
    if (o instanceof String) return listing -> listing.add(escape((String) o));
    if (o instanceof Float) return listing -> listing.add(Locale.US, "%fF", o);
    if (o instanceof Long) return listing -> listing.add(Locale.US, "%dL", o);
    if (o instanceof Character) return listing -> listing.add("'").add(escape((char) o)).add("'");
    return listing -> listing.add(Objects.toString(o));
  }

  private Map<String, List<Listable>> members = Collections.emptyMap();
  private final Name name;

  public Annotation(Name name, Object... values) {
    this.name = requireNonNull(name, "name");
    Arrays.asList(values).forEach(this::addValue);
  }

  public Annotation addMember(String name, Listable listable) {
    requireNonNull(name, "name");
    requireNonNull(listable, "listable");
    List<Listable> values = getMembers().get(name);
    if (values == null) {
      values = new ArrayList<>();
      getMembers().put(name, values);
    }
    values.add(listable);
    return this;
  }

  public Annotation addObject(String memberName, Object object) {
    requireNonNull(object, "constant non-null object expected as value for " + memberName);
    return addMember(memberName, value(object));
  }

  public Annotation addValue(Object object) {
    return addObject("value", object);
  }

  @Override
  public Listing apply(Listing listing) {
    // always emit "@" and the typename
    listing.add('@').add(getTypeName());
    Map<String, List<Listable>> members = getMembers(true);
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
    return getMembers(false);
  }

  public Map<String, List<Listable>> getMembers(boolean readonly) {
    if (members == Collections.EMPTY_MAP && !readonly) {
      members = new LinkedHashMap<>();
    }
    return members;
  }

  public Name getTypeName() {
    return name;
  }

  @Override
  public String toString() {
    return "Annotation{" + getTypeName() + ", members=" + getMembers(true) + "}";
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
