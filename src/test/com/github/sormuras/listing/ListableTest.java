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
      return t.add('a');
    }

    @Override
    public String toString() {
      return "a";
    }
  }

  class Z implements Listable {

    @Override
    public Listing apply(Listing t) {
      return t.add('z');
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
    assertEquals("identity#listing.identity", IDENTITY.comparisonKey());
  }

  @Test
  void empty() {
    assertTrue(IDENTITY.isEmpty());
    assertTrue(NEWLINE.isEmpty());
    assertFalse(SPACE.isEmpty());
    assertFalse(new A().isEmpty());
  }

  @Test
  void list() {
    assertEquals("a", new A().list());
    assertEquals("", IDENTITY.list());
    assertEquals("", NEWLINE.list()); // initial new line is ignored
    assertEquals(" ", SPACE.list());
  }
}
