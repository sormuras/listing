package com.github.sormuras.listing.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.sormuras.listing.Annotation;
import com.github.sormuras.listing.Name;
import org.junit.jupiter.api.Test;

class ClassNameTest {

  @Test
  void annotated() {
    ClassName name = new ClassName("Name", new Annotation(Name.of("", "U")));
    assertEquals("@U Name", name.list());
    name.addAnnotation(Annotation.of(V.class));
    assertEquals("@U @com.github.sormuras.listing.type.V Name", name.list());
  }
}
