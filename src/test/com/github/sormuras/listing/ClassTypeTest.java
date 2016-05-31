package com.github.sormuras.listing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.ElementType;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

public class ClassTypeTest {

  @Test
  public void annotationTarget() {
    assertEquals(ElementType.TYPE_USE, ClassType.of("", "Unnamed").getAnnotationTarget());
  }

  @Test
  public void constructor() {
    assertEquals("Unnamed", ClassType.of("", "Unnamed").list());
    assertEquals("a.b.c.D", ClassType.of("a.b.c", "D").list());
    assertEquals("a.b.c.D.E", ClassType.of("a.b.c", "D", "E").list());
    try {
      ClassType.of("pack.age");
      Assert.fail();
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.toString().contains("pack.age"));
    }
  }

  @Test
  public void enclosingClassType() {
    ClassType state = ClassType.of(Thread.State.class);
    assertEquals("java.lang.Thread.State", state.list());
    assertEquals(ClassType.of(Thread.class), state.getEnclosingClassType().get());
    assertEquals(Optional.empty(), ClassType.of(Thread.class).getEnclosingClassType());
  }

  @Test
  public void unnamedPackage() {
    assertEquals("A", ClassType.of("", "A").toClassName());
  }
}
