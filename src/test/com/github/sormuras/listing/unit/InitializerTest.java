package com.github.sormuras.listing.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class InitializerTest {

  @Test
  void enclosing() {
    Initializer initializer = new Initializer();
    assertEquals(null, initializer.getEnclosing());
  }
}
