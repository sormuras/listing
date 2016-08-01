package com.github.sormuras.listing.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.sormuras.listing.Tests;
import org.junit.jupiter.api.Test;

class CompilationUnitTest {

  @Test
  void topLevelClasses() {
    assertEquals(3, Units.simple().getDeclarations().size());
  }

  @Test
  void simple() {
    String expected = Tests.load(Units.class, "simple");
    String actual = Units.simple().list();
    assertEquals(expected, actual);
  }
}
