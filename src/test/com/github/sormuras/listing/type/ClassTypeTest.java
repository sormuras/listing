package com.github.sormuras.listing.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.annotation.ElementType;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class ClassTypeTest {

  @Test
  void annotationTarget() {
    assertEquals(ElementType.TYPE_USE, ClassType.of("", "Unnamed").getAnnotationTarget());
  }

  @Test
  void constructor() {
    assertEquals("Unnamed", ClassType.of("", "Unnamed").list());
    assertEquals("a.b.c.D", ClassType.of("a.b.c", "D").list());
    assertEquals("a.b.c.D.E", ClassType.of("a.b.c", "D", "E").list());
    assertThrows(AssertionError.class, () -> ClassType.of("pack.age"));
  }

  @Test
  void enclosingClassType() {
    ClassType state = ClassType.of(Thread.State.class);
    assertEquals("java.lang.Thread.State", state.list());
    assertEquals(ClassType.of(Thread.class), state.getEnclosingClassType().get());
    assertEquals(Optional.empty(), ClassType.of(Thread.class).getEnclosingClassType());
  }

  @Test
  void unnamedPackage() {
    assertEquals("A", ClassType.of("", "A").toClassName());
  }
}