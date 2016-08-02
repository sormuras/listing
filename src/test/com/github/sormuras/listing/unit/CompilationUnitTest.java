package com.github.sormuras.listing.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sormuras.listing.Tests;
import org.junit.jupiter.api.Test;

class CompilationUnitTest {

  @Test
  void packageName() {
    assertEquals("", new CompilationUnit().getPackageName());
    assertEquals("a.b.c", new CompilationUnit("a.b.c").getPackageName());
  }

  @Test
  void eponymousDeclaration() {
    // empty unit
    assertFalse(new CompilationUnit().getEponymousDeclaration().isPresent());
    // single type in unit
    CompilationUnit singleTypeUnit = new CompilationUnit();
    singleTypeUnit.declareClass("NonPublic");
    assertTrue(singleTypeUnit.getEponymousDeclaration().isPresent());
    assertEquals("NonPublic", singleTypeUnit.getEponymousDeclaration().get().getName());
    // multiple top level classes
    assertEquals("Gamma", Units.simple().getEponymousDeclaration().get().getName());
  }

  @Test
  void topLevelClasses() {
    assertEquals(3, Units.simple().getDeclarations().size());
  }

  @Test
  void simple() {
    assertEquals(Tests.load(Units.class, "simple"), Units.simple().list());
  }
}
