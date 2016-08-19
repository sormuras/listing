package com.github.sormuras.listing.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.sormuras.listing.Annotation;
import org.junit.jupiter.api.Test;
import test.U;
import test.V;

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
    assertThrows(AssertionError.class, () -> PrimitiveType.of(void.class));
    assertThrows(AssertionError.class, () -> PrimitiveType.of(Byte.class));
  }

  @Test
  void primitiveTypeEqualsAndHashcode() {
    assertEquals(PrimitiveType.of(boolean.class), JavaType.of(boolean.class));
    assertEquals(PrimitiveType.of(byte.class), JavaType.of(byte.class));
    assertEquals(PrimitiveType.of(char.class), JavaType.of(char.class));
    assertEquals(PrimitiveType.of(double.class), JavaType.of(double.class));
    assertEquals(PrimitiveType.of(float.class), JavaType.of(float.class));
    assertEquals(PrimitiveType.of(int.class), JavaType.of(int.class));
    assertEquals(PrimitiveType.of(long.class), JavaType.of(long.class));
    assertEquals(PrimitiveType.of(short.class), JavaType.of(short.class));
    assertNotEquals(PrimitiveType.of(byte.class), JavaType.of(char.class));
    JavaType intAnnotatedWithU = new PrimitiveType.IntType();
    intAnnotatedWithU.addAnnotation(U.class);
    assertNotEquals(intAnnotatedWithU, JavaType.of(int.class));
  }

  @Test
  void primitiveTypeUseWithAnnotation() throws Exception {
    Annotation u = Annotation.of(U.class);
    JavaType uint = JavaType.of(int.class);
    uint.addAnnotation(u);
    assertEquals(U.USE + " int", uint.list());
    JavaType uvint = PrimitiveType.of(int.class);
    uvint.addAnnotation(U.class);
    uvint.addAnnotation(V.class);
    assertEquals(U.USE + " " + V.USE + " int", uvint.list());
    U reflected = U.class.getDeclaredField("NUMBER").getAnnotatedType().getAnnotation(U.class);
    JavaType uint2 = JavaType.of(int.class);
    uint2.addAnnotation(reflected);
    assertEquals(U.USE + " int", uint2.list());
  }
}
