package com.github.sormuras.listing;

import static com.github.sormuras.listing.Listable.IDENTITY;
import static com.github.sormuras.listing.Listable.NEWLINE;
import static com.github.sormuras.listing.Listable.SPACE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class ListableTest {

  class A implements Listable {

    @Override
    public Listing apply(Listing t) {
      return t;
    }

    @Override
    public String toString() {
      return "a";
    }
  }

  class Z implements Listable {

    @Override
    public Listing apply(Listing t) {
      return t;
    }

    @Override
    public String toString() {
      return "z";
    }
  }

  @Test
  void compare() {
    Listable[] expecteds = {new A(), l -> l, new Z()};
    Listable[] actuals = {expecteds[2], expecteds[0], expecteds[1]};
    Arrays.sort(actuals);
    assertArrayEquals(expecteds, actuals);
  }

  @Test
  void comparisonKey() {
    assertEquals("a#a", new A().comparisonKey());
    assertEquals("z#z", new Z().comparisonKey());
    assertTrue(IDENTITY.comparisonKey().startsWith("listable$$lambda"));
  }

  @Test
  void empty() {
    assertTrue(new A().isEmpty());
    assertTrue(IDENTITY.isEmpty());
    assertTrue(NEWLINE.isEmpty());
    assertFalse(SPACE.isEmpty());
  }
}
