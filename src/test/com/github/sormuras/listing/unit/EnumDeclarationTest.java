package com.github.sormuras.listing.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.sormuras.listing.Listable;
import com.github.sormuras.listing.Tests;
import com.github.sormuras.listing.type.ClassType;
import com.github.sormuras.listing.type.JavaType;
import java.lang.annotation.ElementType;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Test;

class EnumDeclarationTest {

  @Test
  void empty() {
    ClassDeclaration declaration = new EnumDeclaration();
    declaration.setName("Empty");
    assertEquals("enum Empty {\n}\n", declaration.list());
  }

  @Test
  void everything() {
    EnumDeclaration declaration = new EnumDeclaration();
    declaration.setName("Everything");
    declaration.addInterface(JavaType.of(Runnable.class));
    assertEquals("enum Everything implements java.lang.Runnable {\n}\n", declaration.list());
    declaration.addInterface(ClassType.of(Comparable.class, Byte.class));
    assertEquals(
        "enum Everything implements java.lang.Runnable, java.lang.Comparable<java.lang.Byte> {\n}\n",
        declaration.list());
    declaration.getInterfaces().clear();
    declaration.addConstant("A");
    assertEquals("enum Everything {\n\n  A\n}\n", declaration.list());
    declaration.addConstant("B", Listable.IDENTITY).addAnnotation(Deprecated.class);
    NormalClassDeclaration cbody = new NormalClassDeclaration();
    MethodDeclaration toString = cbody.declareMethod(String.class, "toString");
    toString.addStatement("return \"c\" + i");
    toString.addModifier(Modifier.PUBLIC);
    declaration.addConstant("C", l -> l.add("123"), cbody);
    declaration.declareField(int.class, "i");
    declaration.declareConstructor().addStatement("this(0)");
    MethodDeclaration ctor = declaration.declareConstructor();
    ctor.addParameter(int.class, "i");
    ctor.addStatement("this.i = i");
    Tests.assertEquals(getClass(), "everything", declaration);
  }

  @Test
  void target() {
    assertEquals(ElementType.TYPE, new EnumDeclaration().getAnnotationTarget());
  }
}
