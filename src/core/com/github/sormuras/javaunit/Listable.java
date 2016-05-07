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
package com.github.sormuras.javaunit;

import static com.github.sormuras.javaunit.Tool.escape;

import java.util.Locale;
import java.util.Objects;
import java.util.function.UnaryOperator;

@FunctionalInterface
public interface Listable extends UnaryOperator<Listing>, Comparable<Listable> {

  static Listable of(Object o) {
    if (o instanceof Class) return listing -> listing.add(JavaName.of((Class<?>) o)).add(".class");
    if (o instanceof Enum) return listing -> listing.add(JavaName.of((Enum<?>) o));
    if (o instanceof String) return listing -> listing.add(escape((String) o));
    if (o instanceof Float) return listing -> listing.add(Locale.US, "%fF", o);
    if (o instanceof Long) return listing -> listing.add(Locale.US, "%dL", o);
    if (o instanceof Character) return listing -> listing.add('\'').add(escape((char) o)).add('\'');
    return listing -> listing.add(Objects.toString(o));
  }

  Listable IDENTITY = listing -> listing;

  Listable NEWLINE = Listing::newline;

  Listable SPACE = listing -> listing.add(' ');

  @Override
  default int compareTo(Listable other) {
    return comparisonKey().compareTo(other.comparisonKey());
  }

  default String comparisonKey() {
    return getClass().getSimpleName() + "#" + toString();
  }

  default boolean isEmpty() {
    return list().isEmpty();
  }

  default String list() {
    return new Listing().add(this).toString();
  }
}
