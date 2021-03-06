package com.github.sormuras.listing.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sormuras.listing.Annotation;
import com.github.sormuras.listing.Listable;
import com.github.sormuras.listing.Name;
import org.junit.jupiter.api.Test;

class VoidTypeTest {

  @Test
  void annotationTargetIsNull() {
    assertNull(new VoidType().getAnnotationTarget());
    assertEquals(Listable.NEWLINE, new VoidType().getAnnotationSeparator());
  }

  @Test
  void annotationsAreImmutable() {
    assertTrue(new VoidType().getAnnotations().isEmpty());
    assertThrows(
        UnsupportedOperationException.class,
        () -> new VoidType().addAnnotation(new Annotation(Name.of("", "Fail"))));
  }

  @Test
  void equalsAndHashCode() {
    assertEquals("void", JavaType.of(void.class).list());
    assertEquals(new VoidType(), JavaType.of(void.class));
    assertEquals(new VoidType().hashCode(), JavaType.of(void.class).hashCode());
    assertFalse(new VoidType().equals(null));
    assertFalse(new VoidType().equals(new Object()));
    VoidType v = new VoidType();
    assertEquals(v, v);
  }
}
