package com.github.sormuras.listing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.annotation.ElementType;

import org.junit.Test;

import com.github.sormuras.listing.ClassType;
import com.github.sormuras.listing.TypeParameter;

public class TypeParameterTest {

  @Test
  public void simple() {
    assertEquals("T", new TypeParameter("T").getName());
    assertEquals("T", new TypeParameter("T").list());
    assertEquals(ElementType.TYPE_PARAMETER, new TypeParameter("T").getAnnotationTarget());
  }

  @Test
  public void boundWithTypeVariable() {
    TypeParameter tp = new TypeParameter("T").setBoundTypeVariable("TV");
    assertEquals("T extends TV", tp.list());
    assertEquals("TV", tp.getBoundTypeVariable().get().getName());
    assertEquals(true, tp.getBounds().isEmpty());
  }

  @Test
  public void boundWithClassType() {
    TypeParameter tp =
        new TypeParameter("T").addBounds(ClassType.of(Number.class), ClassType.of(Cloneable.class));
    assertEquals("T extends java.lang.Number & java.lang.Cloneable", tp.list());
    assertEquals(false, tp.getBoundTypeVariable().isPresent());
    assertEquals(false, tp.getBounds().isEmpty());
  }

  @Test
  public void constructorFailsWithIllegalName() {
    try {
      new TypeParameter("123");
      fail();
    } catch (IllegalArgumentException e) {
      // expected
    }
  }
}
