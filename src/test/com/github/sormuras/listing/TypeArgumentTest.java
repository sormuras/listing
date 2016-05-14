package com.github.sormuras.listing;

import static org.junit.Assert.fail;

import org.junit.Test;

public class TypeArgumentTest {

  @Test
  public void constructorFailsWithWrongJavaType() {
    try {
      new TypeArgument(new PrimitiveType(int.class));
      fail();
    } catch (AssertionError e) {
      // expected
    }
  }
}
