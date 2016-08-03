package com.github.sormuras.listing.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.expectThrows;

import com.github.sormuras.listing.type.TypeVariable;
import java.lang.annotation.ElementType;
import org.junit.jupiter.api.Test;

public class MethodParameterTest {

  @Test
  public void simple() {
    assertEquals("int i", MethodParameter.of(int.class, "i").list());
    assertEquals("int... ia1", MethodParameter.of(int[].class, "ia1").setVariable(true).list());
    assertEquals("int[]... ia2", MethodParameter.of(int[][].class, "ia2").setVariable(true).list());
    assertEquals(
        "final T t",
        new MethodParameter().setType(new TypeVariable("T")).setName("t").setFinal(true).list());
    assertEquals(ElementType.PARAMETER, new MethodParameter().getAnnotationTarget());
    IllegalStateException expected =
        expectThrows(
            IllegalStateException.class,
            () -> MethodParameter.of(int.class, "i").setVariable(true));
    assertEquals(true, expected.toString().contains("array type expected"));
  }
}
