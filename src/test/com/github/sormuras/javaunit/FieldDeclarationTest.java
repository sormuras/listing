package com.github.sormuras.javaunit;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.ElementType;

import org.junit.Test;

public class FieldDeclarationTest {

  @Test
  public void declaration() {
    FieldDeclaration i = new FieldDeclaration().setType(JavaType.of(int.class)).setName("i");
    assertEquals("int i", i.list());
    assertEquals(ElementType.FIELD, i.getAnnotationTarget());
    assertEquals(false, i.isModified());
    i.setInitializer(l -> l.add(Listable.of(4711)));
    assertEquals("int i = 4711;\n", i.list());
  }

}
