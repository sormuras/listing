package com.github.sormuras.listing.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import com.github.sormuras.listing.Annotation;
import com.github.sormuras.listing.Name;

class PrimitiveTypeTest {

  @Test
  void primitiveType() {
    assertEquals("boolean", JavaType.of(boolean.class).list());
    assertEquals("byte", JavaType.of(byte.class).list());
    assertEquals("char", JavaType.of(char.class).list());
    assertEquals("double", JavaType.of(double.class).list());
    assertEquals("float", JavaType.of(float.class).list());
    assertEquals("int", JavaType.of(int.class).list());
    assertEquals("long", JavaType.of(long.class).list());
    assertEquals("short", JavaType.of(short.class).list());
  }

  @Test
  void primitiveTypeEqualsAndHashcode() {
    assertEquals(new PrimitiveType(boolean.class), JavaType.of(boolean.class));
    assertEquals(new PrimitiveType(byte.class), JavaType.of(byte.class));
    assertEquals(new PrimitiveType(char.class), JavaType.of(char.class));
    assertEquals(new PrimitiveType(double.class), JavaType.of(double.class));
    assertEquals(new PrimitiveType(float.class), JavaType.of(float.class));
    assertEquals(new PrimitiveType(int.class), JavaType.of(int.class));
    assertEquals(new PrimitiveType(long.class), JavaType.of(long.class));
    assertEquals(new PrimitiveType(short.class), JavaType.of(short.class));
    assertNotEquals(new PrimitiveType(byte.class), JavaType.of(char.class));
  }

  @Test
  void primitiveTypeUseWithAnnotation() throws Exception {
    Annotation u = new Annotation(Name.of("com.github.sormuras.listing.type", "U"));
    JavaType uint = JavaType.of(int.class);
    uint.addAnnotation(u);
    assertEquals(U.USE + " int", uint.list());
    JavaType uvint = new PrimitiveType(int.class);
    uvint.addAnnotation(Annotation.of(U.class));
    uvint.addAnnotation(Annotation.of(V.class));
    assertEquals(U.USE + " " + V.USE + " int", uvint.list());
    U reflected = U.class.getDeclaredField("NUMBER").getAnnotatedType().getAnnotation(U.class);
    JavaType uint2 = JavaType.of(int.class);
    uint2.addAnnotation(Annotation.of(reflected));
    assertEquals(U.USE + " int", uint2.list());
  }
}
