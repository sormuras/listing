package com.github.sormuras.listing.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.github.sormuras.listing.type.JavaType;
import java.lang.annotation.ElementType;
import org.junit.jupiter.api.Test;

class FieldDeclarationTest {

  @Test
  void empty() {
    FieldDeclaration i = new FieldDeclaration();
    i.setType(JavaType.of(int.class));
    i.setName("i");
    assertEquals("int i;\n", i.list());
    assertEquals(ElementType.FIELD, i.getAnnotationTarget());
    assertEquals(false, i.isModified());
    assertNull(i.getEnclosingDeclaration());
    i.setInitializer(l -> l.add(Integer.toString(4711)));
    assertEquals("int i = 4711;\n", i.list());
  }
}
