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
    assertEquals("T", new TypeParameter().getName());
    assertEquals("T", TypeParameter.of("T").list());
    assertEquals(ElementType.TYPE_PARAMETER, new TypeParameter().getAnnotationTarget());
    assertEquals("T extends S", TypeParameter.of("T", "S").list());
    JavaType run = ClassType.of(Runnable.class);
    assertEquals("S extends java.lang.Runnable", TypeParameter.of("S", run).list());
    // annotated
    TypeParameter parameter = new TypeParameter();
    parameter.addAnnotation(new Annotation(Name.of("", "A")));
    assertEquals("@A T", parameter.list());
  }

  @Test
  void boundWithTypeVariable() {
    TypeParameter tp = TypeParameter.of("T", "TV");
    assertEquals("T extends TV", tp.list());
    assertEquals("TV", tp.getBoundTypeVariable().get().getName());
    assertEquals(true, tp.getBounds().isEmpty());
  }

  @Test
  void boundWithClassType() {
    TypeParameter tp = new TypeParameter();
    tp.addBounds(ClassType.of(Number.class), ClassType.of(Cloneable.class));
    tp.addBounds(new ClassType[0]);
    assertEquals("T extends java.lang.Number & java.lang.Cloneable", tp.list());
    assertEquals(false, tp.getBoundTypeVariable().isPresent());
    assertEquals(false, tp.getBounds().isEmpty());
    // clears bounds by setting bound type variable
    tp.setBoundTypeVariable("S");
    assertEquals(true, tp.getBoundTypeVariable().isPresent());
    assertEquals(true, tp.getBounds().isEmpty());
  }

  @Test
  void constructorFailsWithIllegalName() {
    expectThrows(AssertionError.class, () -> TypeParameter.of("123"));
  }
}
