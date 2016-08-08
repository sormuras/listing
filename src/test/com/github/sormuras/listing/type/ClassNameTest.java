package com.github.sormuras.listing.type;

import static com.github.sormuras.listing.Tests.assertSerializable;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.sormuras.listing.Annotation;
import com.github.sormuras.listing.Name;
import org.junit.jupiter.api.Test;

class ClassNameTest {

  @Test
  void annotated() throws Exception {
    ClassName name = new ClassName();
    name.setName("Name");
    assertEquals("Name", name.list());
    name.addAnnotation(new Annotation(Name.of("", "UUU")));
    assertEquals("@UUU Name", name.list());
    name.addAnnotation(V.class);
    assertEquals("@UUU @com.github.sormuras.listing.type.V Name", name.list());
    assertSerializable(name);
  }
}
