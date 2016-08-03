package com.github.sormuras.listing.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.expectThrows;

import org.junit.jupiter.api.Test;

class TypeVariableTest {

  @Test
  void defaultsToNameT() {
    assertEquals("T", new TypeVariable().getName());
  }

  @Test
  void constructorFailsWithEmptyName() {
    Exception e = expectThrows(IllegalArgumentException.class, () -> new TypeVariable(""));
    assertTrue(e.toString().contains("TypeVariable name must not be empty!"));
  }
}
