package com.github.sormuras.listing;

import static org.junit.gen5.api.Assertions.assertEquals;
import static org.junit.gen5.api.Assertions.assertNotEquals;
import static org.junit.gen5.api.Assertions.assertTrue;

import org.junit.gen5.api.Test;

class NameTest {

  @Test
  void create() {
    assertEquals("abc", new Name("abc").getIdentifiers()[0]);
    assertEquals("abc", new Name("abc").getQualified());
  }

  @Test
  void equals() {
    Name name = new Name("abc");
    assertTrue(name.equals(name));
    assertEquals(name, name);
    assertEquals(name, new Name("abc"));
    assertNotEquals(name, null);
    assertNotEquals(name, new Object());
    assertNotEquals(name, new Name("xyz"));
  }

  @Test
  void string() {
    assertEquals("Name[abc]", new Name("abc").toString());
    assertEquals("Name[a.b]", new Name("a", "b").toString());
    assertEquals("Name[a.b.C]", new Name("a", "b", "C").toString());
  }
}
