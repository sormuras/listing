package com.github.sormuras.listing.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.sormuras.listing.Name;
import com.github.sormuras.listing.Tests;
import com.github.sormuras.listing.type.JavaType;
import java.lang.annotation.ElementType;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class InterfaceDeclarationTest {

  @Test
  void empty() {
    TypeDeclaration declaration = new InterfaceDeclaration();
    declaration.setName("Empty");
    assertEquals("interface Empty {\n}\n", declaration.list());
  }

  @Test
  void everything() {
    TypeParameter t = new TypeParameter();
    InterfaceDeclaration declaration = new InterfaceDeclaration();
    declaration.setName("Everything");
    declaration.getTypeParameters().add(t);
    declaration.addInterface(JavaType.of(Runnable.class));
    declaration.addConstant(JavaType.of(String.class), "EMPTY_TEXT", "");
    declaration
        .addConstant(JavaType.of(float.class), "PI", l -> l.add("3.141F"))
        .addAnnotation(Deprecated.class);
    declaration.addConstant(JavaType.of(double.class), "E", Name.of(Math.class, "E"));
    declaration.declareMethod(BigInteger.class, "id");
    Tests.assertEquals(getClass(), "everything", declaration);
  }

  @Test
  void target() {
    assertEquals(ElementType.TYPE, new InterfaceDeclaration().getAnnotationTarget());
  }
}
