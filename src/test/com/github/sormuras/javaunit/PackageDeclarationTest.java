package com.github.sormuras.javaunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.annotation.ElementType;

import org.junit.Test;

public class PackageDeclarationTest {

  @Test
  public void list() {
    // unnamed
    assertEquals("", new PackageDeclaration().list());
    // simple
    assertEquals("package abc;\n", new PackageDeclaration("abc").list());
    assertEquals("package abc.xyz;\n", new PackageDeclaration("abc.xyz").list());
    // with annotation(s)
    Text.assertEquals(
        getClass(),
        "annotated",
        new PackageDeclaration("abc.xyz")
            .addAnnotation(new JavaAnnotation(JavaName.of("abc", "PackageAnnotation"))));
  }

  @Test
  public void separator() {
    assertEquals(Listable.NEWLINE, new PackageDeclaration().getAnnotationSeparator());
  }

  @Test
  public void target() {
    assertEquals(ElementType.PACKAGE, new PackageDeclaration("t").getAnnotationTarget());
  }

  @Test
  public void unnamedAsStringFails() {
    try {
      new PackageDeclaration("");
      fail("unnamed package declaration created?!");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.toString().contains("empty"));
    }
  }
}
