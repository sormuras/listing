package com.github.sormuras.listing;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.github.sormuras.listing.PrimitiveType;
import com.github.sormuras.listing.TypeArgument;

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
