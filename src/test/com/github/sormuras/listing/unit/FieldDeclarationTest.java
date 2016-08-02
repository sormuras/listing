package com.github.sormuras.listing.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.sormuras.listing.type.JavaType;
import java.lang.annotation.ElementType;
import org.junit.jupiter.api.Test;

class FieldDeclarationTest {

  @Test
  void declaration() {
    FieldDeclaration i = new FieldDeclaration().setType(JavaType.of(int.class)).setName("i");
    assertEquals("int i;\n", i.list());
    assertEquals(ElementType.FIELD, i.getAnnotationTarget());
    assertEquals(false, i.isModified());
    assertEquals(false, i.getEnclosingDeclaration().isPresent());
    i.setInitializer(l -> l.add(Integer.toString(4711)));
    assertEquals("int i = 4711;\n", i.list());
  }
}
