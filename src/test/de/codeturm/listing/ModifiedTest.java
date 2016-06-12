package de.codeturm.listing;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import javax.lang.model.element.Modifier;

import org.junit.Test;

public class ModifiedTest implements Modified {

  private final Set<Modifier> modifiers = new TreeSet<>();

  @Test
  public void addModifiers() {
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
  public void addModifiersFails() {
    try {
      addModifier(Modifier.VOLATILE);
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("VOLATILE"));
    }
  }

  @Test
  public void emptyOnCreation() {
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

  @Test
  public void validationSetDefaultsToAllEnumConstants() {
    Set<Modifier> set = Modified.super.getModifierValidationSet();
    assertTrue(set.containsAll(EnumSet.allOf(Modifier.class)));
    assertTrue(set.equals(EnumSet.allOf(Modifier.class)));
  }
}
