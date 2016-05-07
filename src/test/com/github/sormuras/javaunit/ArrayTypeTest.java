package com.github.sormuras.javaunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class ArrayTypeTest {

  @Test
  public void arrayType() {
    assertEquals("byte[]", new ArrayType(JavaType.of(byte.class), 1).list());
    assertEquals("byte[][][]", new ArrayType(JavaType.of(byte.class), 3).list());
    assertEquals("byte[][][]", JavaType.of(byte[][][].class).list());
  }

  @Test
  public void arrayTypeWithAnnotatedDimensions() {
    ArrayType actual = new ArrayType(JavaType.of(byte.class), 3);
    actual.addAnnotations(0, new JavaAnnotation(JavaName.of("test", "T")));
    actual.addAnnotations(
        1,
        new JavaAnnotation(JavaName.of("test", "S")),
        new JavaAnnotation(JavaName.of("test", "T")));
    actual.addAnnotations(2, new JavaAnnotation(JavaName.of("test", "T")));
    assertEquals("byte@test.T []@test.S @test.T []@test.T []", actual.list());
    assertSame(actual.getAnnotations(), actual.getDimensions().get(0).getAnnotations());
  }
}
