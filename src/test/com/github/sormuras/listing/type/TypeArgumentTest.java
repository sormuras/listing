package com.github.sormuras.listing.type;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.expectThrows;

import org.junit.jupiter.api.Test;

class TypeArgumentTest {

  @Test
  void constructorFailsWithWrongJavaType() {
    AssertionError e =
        expectThrows(AssertionError.class, () -> new TypeArgument(new PrimitiveType(int.class)));
    assertTrue(e.toString().contains("neither reference nor wildcard set"));
  }
}
