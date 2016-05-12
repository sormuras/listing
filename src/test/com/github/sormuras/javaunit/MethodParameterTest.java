package com.github.sormuras.javaunit;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.ElementType;

import org.junit.Test;

public class MethodParameterTest {

  @Test
  public void simple() {
    assertEquals("T t",
        new MethodParameter().setType(new TypeVariable().setName("T")).setName("t").list());
    assertEquals("final int i",
        new MethodParameter().setType(JavaType.of(int.class)).setName("i").setFinal(true).list());
    assertEquals("int... ia1",
        new MethodParameter().setType(JavaType.of(int[].class))
            .setName("ia1")
            .setVariable(true)
            .list());
    assertEquals("int[][]... ia3",
        new MethodParameter().setType(JavaType.of(int[][][].class))
        .setName("ia3")
        .setVariable(true)
        .list());
    assertEquals(ElementType.PARAMETER, new MethodParameter().getAnnotationTarget());
  }
}
