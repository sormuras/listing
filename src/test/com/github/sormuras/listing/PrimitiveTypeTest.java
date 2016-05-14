package com.github.sormuras.listing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class PrimitiveTypeTest {

  @Test
  public void primitiveType() {
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
  public void primitiveTypeEqualsAndHashcode() {
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
  public void primitiveTypeUseWithAnnotation() throws Exception {
    JavaType<?> uint = JavaType.of(int.class).addAnnotation("com.github.sormuras.listing", "U");
    assertEquals(U.USE + " int", uint.list());
    JavaType<?> uvint = new PrimitiveType(int.class).addAnnotation(U.class).addAnnotation(V.class);
    assertEquals(U.USE + " " + V.USE + " int", uvint.list());
    U crazy = U.class.getDeclaredField("NUMBER").getAnnotatedType().getAnnotation(U.class);
    JavaType<?> uint2 = JavaType.of(int.class).addAnnotation(crazy);
    assertEquals(U.USE + " int", uint2.list());
  }
}
