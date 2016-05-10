package com.github.sormuras.javaunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.ElementType;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

public class ClassTypeTest {

  @Test
  public void annotationTarget() {
    assertEquals(ElementType.TYPE_USE, new ClassType("", "Unnamed").getAnnotationTarget());
  }

  @Test
  public void constructor() {
    assertEquals("Unnamed", new ClassType("", "Unnamed").list());
    assertEquals("a.b.c.D", new ClassType("a.b.c", "D").list());
    assertEquals("a.b.c.D.E", new ClassType("a.b.c", "D", "E").list());
    try {
      new ClassType("pack.age");
      Assert.fail();
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.toString().contains("pack.age"));
    }
  }

  @Test
  public void enclosingClassType() {
    ClassType state = new ClassType(Thread.State.class);
    assertEquals("java.lang.Thread.State", state.list());
    assertEquals(new ClassType(Thread.class), state.getEnclosingClassType().get());
    assertEquals(Optional.empty(), new ClassType(Thread.class).getEnclosingClassType());
  }
}
