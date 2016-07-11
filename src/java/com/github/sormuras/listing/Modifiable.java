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
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import javax.lang.model.element.Modifier;

/** Default {@link Modifier} support. */
public interface Modifiable {

  default void addModifier(Modifier modifier) {
    validateModifiers(modifier);
    getModifiers().add(modifier);
  }

  default void addModifiers(Collection<Modifier> modifiers) {
    validateModifiers(modifiers.toArray(new Modifier[modifiers.size()]));
    getModifiers().addAll(modifiers);
  }

  default void addModifiers(int mod) {
    addModifiers(Tool.modifiers(mod));
  }

  default void addModifiers(Modifier... modifiers) {
    validateModifiers(modifiers);
    getModifiers().addAll(Arrays.asList(modifiers));
  }

  /**
   * Returns all applied modifiers.
   *
   * @return Applied modifiers.
   */
  Set<Modifier> getModifiers();

  /**
   * Returns all modifiers that are applicable to this element kind.
   *
   * @return All modifiers defined in {@link Modifier}.
   */
  default Set<Modifier> getModifierValidationSet() {
    return EnumSet.allOf(Modifier.class);
  }

  default boolean isModified() {
    return !getModifiers().isEmpty();
  }

  default boolean isStatic() {
    return getModifiers().contains(Modifier.STATIC);
  }

  default void setModifiers(int mod) {
    getModifiers().clear();
    addModifiers(mod);
  }

  default void setModifiers(Modifier... modifiers) {
    getModifiers().clear();
    addModifiers(modifiers);
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
      if (!set.contains(modifier)) {
        String message = "Modifier %s not allowed at instance of %s, valid modifier(s): %s";
        throw new IllegalArgumentException(
            String.format(message, modifier.name(), getClass(), set));
      }
    }
  }
}
