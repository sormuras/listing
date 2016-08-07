package com.github.sormuras.listing.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sormuras.listing.type.ClassType;
import com.github.sormuras.listing.type.JavaType;
import java.lang.annotation.ElementType;
import org.junit.jupiter.api.Test;

class NormalClassDeclarationTest {

  @Test
  void empty() {
    NormalClassDeclaration declaration = new NormalClassDeclaration();
    declaration.setName("Empty");
    assertEquals("class Empty {\n}\n", declaration.list());
    assertTrue(declaration.isEmpty());
    assertFalse(new NormalClassDeclaration().declareInitializer(false).getEnclosing().isEmpty());
    assertFalse(
        new NormalClassDeclaration().declareField(int.class, "i").getEnclosing().get().isEmpty());
  }

  @Test
  void generic() {
    NormalClassDeclaration declaration = new NormalClassDeclaration();
    declaration.setName("G");
    declaration.addTypeParameter(new TypeParameter("T"));
    assertEquals("class G<T> {\n}\n", declaration.list());
    declaration.addTypeParameter(
        new TypeParameter("I").addBounds(ClassType.of(Iterable.class, Long.class)));
    assertEquals(
        "class G<T, I extends java.lang.Iterable<java.lang.Long>> {\n}\n", declaration.list());
  }

  @Test
  void interfaces() {
    NormalClassDeclaration declaration = new NormalClassDeclaration();
    declaration.setName("I");
    declaration.addInterface(JavaType.of(Runnable.class));
    assertEquals("class I implements java.lang.Runnable {\n}\n", declaration.list());
    declaration.addInterface(ClassType.of(Comparable.class, Byte.class));
    assertEquals(
        "class I implements java.lang.Runnable, java.lang.Comparable<java.lang.Byte> {\n}\n",
        declaration.list());
  }

  @Test
  void superclass() {
    NormalClassDeclaration declaration = new NormalClassDeclaration();
    declaration.setName("C");
    declaration.setSuperClass(ClassType.of(Object.class));
    assertEquals("class C extends java.lang.Object {\n}\n", declaration.list());
  }

  @Test
  void target() {
    assertEquals(ElementType.TYPE, new NormalClassDeclaration().getAnnotationTarget());
  }
}
