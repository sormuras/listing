package com.github.sormuras.listing.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.expectThrows;

import com.github.sormuras.listing.type.ClassType;
import com.github.sormuras.listing.type.JavaType;
import java.lang.annotation.ElementType;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class EnumDeclarationTest {

  @Test
  void empty() {
    ClassDeclaration declaration = new EnumDeclaration();
    declaration.setName("Empty");
    assertEquals("enum Empty {\n}\n", declaration.list());
  }

  @Test
  void generic() {
    assertEquals(Collections.EMPTY_LIST, new EnumDeclaration().getTypeParameters());
    expectThrows(
        UnsupportedOperationException.class,
        () -> new EnumDeclaration().addTypeParameter(new TypeParameter()));
  }

  @Test
  void interfaces() {
    ClassDeclaration declaration = new EnumDeclaration();
    declaration.setName("E");
    declaration.addInterface(JavaType.of(Runnable.class));
    assertEquals("enum E implements java.lang.Runnable {\n}\n", declaration.list());
    declaration.addInterface(ClassType.of(Comparable.class, Byte.class));
    assertEquals(
        "enum E implements java.lang.Runnable, java.lang.Comparable<java.lang.Byte> {\n}\n",
        declaration.list());
  }

  @Test
  void superclass() {
    assertEquals(ClassType.of(Enum.class), new EnumDeclaration().getSuperClass());
    expectThrows(
        UnsupportedOperationException.class,
        () -> new EnumDeclaration().setSuperClass(ClassType.of(Object.class)));
  }

  @Test
  void target() {
    assertEquals(ElementType.TYPE, new EnumDeclaration().getAnnotationTarget());
  }
}
