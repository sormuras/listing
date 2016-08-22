package com.github.sormuras.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NameTest {

  @Test
  void test() {
    Assertions.assertEquals("a", Name.name("a").packageName());
    Assertions.assertEquals("a", Name.name("a").canonical());
    Assertions.assertEquals("a.b", Name.name("a.b").packageName());
    Assertions.assertEquals("a.b", Name.name("a.b").canonical());
    Assertions.assertEquals("a.b", Name.name("a.b", "C").packageName());
    Assertions.assertEquals("a.b.C", Name.name("a.b", "C").canonical());
    Assertions.assertEquals("java.lang", Name.name(Object.class).packageName());
    Assertions.assertEquals("java.lang.Object", Name.name(Object.class).canonical());
  }

  @Test
  void enclosing() {
    Name state = Name.name(Thread.State.class);
    Assertions.assertEquals("java.lang.Thread.State", state.canonical());
    Name thread = state.enclosing();
    Assertions.assertEquals("java.lang.Thread", thread.canonical());
    Name lang = thread.enclosing();
    Assertions.assertEquals("java.lang", lang.canonical());
    Name java = lang.enclosing();
    Assertions.assertEquals("java", java.canonical());
    Assertions.assertThrows(IllegalArgumentException.class, () -> java.enclosing());
  }
}
