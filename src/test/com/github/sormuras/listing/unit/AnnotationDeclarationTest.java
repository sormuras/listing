package com.github.sormuras.listing.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.sormuras.listing.Name;
import com.github.sormuras.listing.Tests;
import com.github.sormuras.listing.type.ClassType;
import com.github.sormuras.listing.type.JavaType;
import com.github.sormuras.listing.type.TypeArgument;
import com.github.sormuras.listing.type.WildcardType;
import java.lang.annotation.ElementType;
import java.util.Formatter;
import org.junit.jupiter.api.Test;

class AnnotationDeclarationTest {

  @Test
  void empty() {
    TypeDeclaration declaration = new AnnotationDeclaration();
    assertEquals("@interface AnnotationDeclaration {\n}\n", declaration.list());
  }

  @Test
  void requestForEnhancement() {
    WildcardType extendsFormatter = new WildcardType();
    extendsFormatter.setBoundExtends(ClassType.of(Formatter.class));
    AnnotationDeclaration declaration = new AnnotationDeclaration("RequestForEnhancement");
    declaration.addElement(JavaType.of(int.class), "id");
    declaration
        .addElement(JavaType.of(String.class), "date", "201608032129")
        .addAnnotation(Deprecated.class);
    declaration.addElement(
        new ClassType(Name.of(Class.class), new TypeArgument(extendsFormatter)), "formatterClass");
    Tests.assertEquals(getClass(), "requestForEnhancement", declaration);
  }

  @Test
  void target() {
    assertEquals(ElementType.TYPE, new AnnotationDeclaration().getAnnotationTarget());
  }
}
