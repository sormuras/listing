package com.github.sormuras.listing.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.github.sormuras.listing.Annotation;
import com.github.sormuras.listing.Name;
import org.junit.jupiter.api.Test;

class ArrayTypeTest {

  @Test
  void arrayType() {
    assertEquals("byte[]", ArrayType.of(JavaType.of(byte.class), 1).list());
    assertEquals("byte[][][]", ArrayType.of(JavaType.of(byte.class), 3).list());
    assertEquals("byte[][][]", JavaType.of(byte[][][].class).list());
  }

  @Test
  void arrayTypeWithAnnotatedDimensions() {
    ArrayType actual = ArrayType.of(JavaType.of(byte.class), 3);
    actual.addAnnotations(0, new Annotation(Name.of("test", "T")));
    actual.addAnnotations(
        1, new Annotation(Name.of("test", "S")), new Annotation(Name.of("test", "T")));
    actual.addAnnotations(2, new Annotation(Name.of("test", "T")));
    assertEquals("byte@test.T []@test.S @test.T []@test.T []", actual.list());
    assertSame(actual.getAnnotations(), actual.getDimensions().get(0).getAnnotations());
  }

  @Test
  void mutable() {
    ArrayType array = new ArrayType();
    assertEquals(true, array.isEmpty());
    assertEquals(false, array.isAnnotated());
    assertEquals(true, array.getAnnotations().isEmpty());
    assertEquals(true, array.getDimensions().isEmpty());
    array.getDimensions().add(new ArrayDimension());
    assertEquals(false, array.getDimensions().isEmpty());
    assertEquals(1, array.getDimensions().size());
    array.setComponentType(new PrimitiveType.IntType());
    assertEquals("int[]", array.list());
    array.addAnnotation(new Annotation(Name.of("test", "T")));
    assertEquals(1, array.getAnnotations().size());
    assertEquals("int@test.T []", array.list());
  }
}
