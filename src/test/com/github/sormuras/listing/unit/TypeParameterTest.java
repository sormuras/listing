package com.github.sormuras.listing.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.expectThrows;

import com.github.sormuras.listing.Annotation;
import com.github.sormuras.listing.Name;
import com.github.sormuras.listing.type.ClassType;
import com.github.sormuras.listing.type.JavaType;
import java.lang.annotation.ElementType;
import org.junit.jupiter.api.Test;

class TypeParameterTest {

  @Test
  void simple() {
    assertEquals("T", new TypeParameter("T").getName());
    assertEquals("T", new TypeParameter("T").list());
    assertEquals(ElementType.TYPE_PARAMETER, new TypeParameter("T").getAnnotationTarget());
    assertEquals("T extends S", new TypeParameter("T").setBoundTypeVariable("S").list());
    JavaType run = ClassType.of(Runnable.class);
    assertEquals("S extends java.lang.Runnable", new TypeParameter("S").addBounds(run).list());
    // annotated
    TypeParameter parameter = new TypeParameter("T");
    parameter.addAnnotation(new Annotation(Name.of("", "A")));
    assertEquals("@A T", parameter.list());
  }

  @Test
  void boundWithTypeVariable() {
    TypeParameter tp = new TypeParameter("T").setBoundTypeVariable("TV");
    assertEquals("T extends TV", tp.list());
    assertEquals("TV", tp.getBoundTypeVariable().get().getName());
    assertEquals(true, tp.getBounds().isEmpty());
  }

  @Test
  void boundWithClassType() {
    TypeParameter tp = new TypeParameter("T");
    tp.addBounds(ClassType.of(Number.class), ClassType.of(Cloneable.class));
    assertEquals("T extends java.lang.Number & java.lang.Cloneable", tp.list());
    assertEquals(false, tp.getBoundTypeVariable().isPresent());
    assertEquals(false, tp.getBounds().isEmpty());
  }

  @Test
  void constructorFailsWithIllegalName() {
    expectThrows(AssertionError.class, () -> new TypeParameter("123"));
  }
}
