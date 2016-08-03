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

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import javax.lang.model.element.Modifier;

/** Default {@link Modifier} set support. */
public interface Modifiable {

  /** Add modifier to the set. */
  default void addModifier(Modifier modifier) {
    validateModifiers(modifier);
    getModifiers().add(modifier);
  }

  /** Add variable array of modifier names to the set. */
  default void addModifier(String... modifiers) {
    asList(modifiers).forEach(name -> addModifier(Modifier.valueOf(name.toUpperCase())));
  }

  /** Add collection of modifiers to the set. */
  default void addModifiers(Collection<Modifier> modifiers) {
    validateModifiers(modifiers.toArray(new Modifier[modifiers.size()]));
    getModifiers().addAll(modifiers);
  }

  /** Add collection of modifiers to the set. */
  default void addModifiers(int mod) {
    addModifiers(Tool.modifiers(mod));
  }

  /** Add variable array of modifiers to the set. */
  default void addModifiers(Modifier... modifiers) {
    validateModifiers(modifiers);
    getModifiers().addAll(asList(modifiers));
  }

  /**
   * Returns all applied modifiers.
   *
   * @return Applied modifiers.
   */
  default Set<Modifier> getModifiers() {
    return getModifiers(false);
  }

  /** Return set of modifiers indicating if the caller will mutate the set. */
  Set<Modifier> getModifiers(boolean readonly);

  /**
   * Returns all modifiers that are applicable to this element kind.
   *
   * @return All modifiers defined in {@link Modifier}.
   */
  default Set<Modifier> getModifierValidationSet() {
    return EnumSet.allOf(Modifier.class);
  }

  /** Return {@code true} if modifier set is not empty, else {@code false}. */
  default boolean isModified() {
    return !getModifiers(true).isEmpty();
  }

  /** Return {@code true} if {@link Modifier#PUBLIC} is part of modifier set, else {@code false}. */
  default boolean isPublic() {
    return getModifiers(true).contains(Modifier.PUBLIC);
  }

  /** Return {@code true} if {@link Modifier#STATIC} is part of modifier set, else {@code false}. */
  default boolean isStatic() {
    return getModifiers(true).contains(Modifier.STATIC);
  }

  /** Replace current modifiers by with new ones. */
  default void setModifiers(int mod) {
    getModifiers().clear();
    addModifiers(mod);
  }

  /** Replace current modifiers by with new ones. */
  default void setModifiers(Modifier... modifiers) {
    getModifiers().clear();
    addModifiers(modifiers);
  }

  /** Return modifiers as an inline listable. */
  default Listable toModifiersListable() {
    return listing -> {
      getModifiers(true).forEach(m -> listing.add(m.name().toLowerCase()).add(' '));
      return listing;
    };
  }

  /**
   * Tests whether modifiers are applicable to this element kind.
   *
   * @param modifiers Modifiers to test.
   * @see #getModifierValidationSet()
   */
  default void validateModifiers(Modifier... modifiers) {
    Set<Modifier> set = getModifierValidationSet();
    for (Modifier modifier : modifiers) {
      requireNonNull(modifier, "null is not a valid modifier");
      if (!set.contains(modifier)) {
        String message = "Modifier %s not allowed at instance of %s, valid modifier(s): %s";
        throw new IllegalArgumentException(String.format(message, modifier, getClass(), set));
      }
    }
  }
}
