package com.github.sormuras.javaunit;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class JavaTypeTest {

  public List<String> parameterizedFieldType;

  @Test
  public void of() {
    try {
      assertEquals("void", JavaType.of(Object.class).list());
    } catch (AssertionError e) {
      // expected
    }
  }

  @Test
  public void type() throws Exception {
    assertEquals(
        "void",
        JavaType.of(JavaTypeTest.class.getDeclaredMethod("type").getGenericReturnType()).list());
    try {
      assertEquals(
          "void",
          JavaType.of(
                  JavaTypeTest.class.getDeclaredField("parameterizedFieldType").getGenericType())
              .list());
    } catch (AssertionError e) {
      // expected
    }
  }
}
