package com.github.sormuras.listing;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.expectThrows;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Test;

class ModifiableTest implements Modifiable {

  private final Set<Modifier> modifiers = new TreeSet<>();

  @Test
  void addModifiers() {
    addModifier(ABSTRACT);
    assertTrue(getModifiers().equals(EnumSet.of(ABSTRACT)));
    assertFalse(isStatic());
    addModifiers(PUBLIC, STATIC);
    assertTrue(getModifiers().equals(EnumSet.of(ABSTRACT, PUBLIC, STATIC)));
    assertTrue(isStatic());
    addModifiers(Arrays.asList(FINAL));
    assertTrue(getModifiers().equals(EnumSet.of(ABSTRACT, FINAL, PUBLIC, STATIC)));
    assertTrue(isStatic());
  }

  @Test
  void addModifiersFails() {
    Modifier invalid = Modifier.VOLATILE;
    Exception e = expectThrows(IllegalArgumentException.class, () -> addModifier(invalid));
    assertTrue(e.getMessage().contains(invalid.toString()));
  }

  @Test
  void emptyOnCreation() {
    assertFalse(isStatic());
    assertTrue(getModifiers().isEmpty());
  }

  @Override
  public Set<Modifier> getModifiers() {
    return modifiers;
  }

  @Override
  public Set<Modifier> getModifierValidationSet() {
    return EnumSet.of(ABSTRACT, FINAL, PUBLIC, STATIC);
  }

  @Override
  public boolean isModified() {
    return !modifiers.isEmpty();
  }

  @Test
  void validationSetDefaultsToAllEnumConstants() {
    Set<Modifier> set = Modifiable.super.getModifierValidationSet();
    assertTrue(set.containsAll(EnumSet.allOf(Modifier.class)));
    assertTrue(set.equals(EnumSet.allOf(Modifier.class)));
  }

  @Test
  void validationThrowsNullPointerException() {
    expectThrows(NullPointerException.class, () -> validateModifiers(null, null));
  }
}
