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

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import javax.lang.model.element.Modifier;

/**
 * Provides default methods for handling a set of {@linkplain Modifier}s.
 *
 * @author Christian Stein
 */
public interface Modified<T> extends Listable {

  @SuppressWarnings("unchecked")
  default T addModifier(Modifier... modifiers) {
    Collections.addAll(getModifiers(), modifiers);
    return (T) this;
  }

  @SuppressWarnings("unchecked")
  default T addModifier(String... modifiers) {
    Arrays.asList(modifiers).forEach(name -> addModifier(Modifier.valueOf(name.toUpperCase())));
    return (T) this;
  }

  Set<Modifier> getModifiers();

  default boolean isModified() {
    return !getModifiers().isEmpty();
  }

  default boolean isPublic() {
    return getModifiers().contains(Modifier.PUBLIC);
  }

  default Listable toModifiersListable() {
    return listing -> {
      getModifiers().forEach(m -> listing.add(m.name().toLowerCase()).add(' '));
      return listing;
    };
  }
}
