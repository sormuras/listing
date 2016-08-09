package com.github.sormuras.listing.type;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.expectThrows;

import org.junit.jupiter.api.Test;

class TypeArgumentTest {

  @Test
  void constructor() {
    TypeArgument argumentWithReference = new TypeArgument(Object.class);
    assertNotNull(argumentWithReference.getReference());
    assertNull(argumentWithReference.getWildcard());
    TypeArgument argumentWithWildcard = new TypeArgument(new WildcardType());
    assertNull(argumentWithWildcard.getReference());
    assertNotNull(argumentWithWildcard.getWildcard());
  }

  @Test
  void constructorFailsWithWrongJavaType() {
    AssertionError e =
        expectThrows(AssertionError.class, () -> new TypeArgument(PrimitiveType.of(int.class)));
    assertTrue(e.toString().contains("neither reference nor wildcard set"));
  }
}
