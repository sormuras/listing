package com.github.sormuras.listing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.sormuras.listing.JavaType;
import com.github.sormuras.listing.VoidType;

public class VoidTypeTest {

  @Test
  public void annotationsAreImmutable() {
    assertTrue(new VoidType().getAnnotations().isEmpty());
    try {
      new VoidType().addAnnotation("", "Fail");
    } catch (Exception e) {
      // expected
    }
  }

  @Test
  public void equalsAndHashCode() {
    assertEquals("void", JavaType.of(void.class).list());
    assertEquals(new VoidType(), JavaType.of(void.class));
    assertEquals(new VoidType().hashCode(), JavaType.of(void.class).hashCode());
    assertFalse(new VoidType().equals(null));
    assertFalse(new VoidType().equals(new Object()));
    VoidType v = new VoidType();
    assertEquals(v, v);
  }
}
