package com.github.sormuras.java;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class NameTest {

  @Test
  void array() {
    assertEquals("int[][]", Name.name(int[][].class).canonical());
  }

  @Test
  void enclosed() {
    Name.name(void.class);
    Name state = Name.name(Thread.State.class);
    assertEquals("java.lang.Thread.State", state.canonical());
    assertTrue(state.isEnclosed());
    assertTrue(state.isJavaLangPackage());

    Name thread = state.enclosing();
    assertEquals("java.lang.Thread", thread.canonical());
    assertTrue(thread.isEnclosed());
    assertTrue(state.isJavaLangPackage());

    Name lang = thread.enclosing();
    assertEquals("java.lang", lang.canonical());
    assertTrue(lang.isEnclosed());
    assertTrue(state.isJavaLangPackage());

    Name java = lang.enclosing();
    assertEquals("java", java.canonical());
    assertFalse(java.isEnclosed());
    assertFalse(java.isJavaLangPackage());
    assertThrows(IllegalStateException.class, java::enclosing);
  }

  @Test
  void name() {
    assertEquals("A", Name.name("A").canonical());
    assertEquals("", Name.name("A").packageName());
    assertEquals("A", Name.name("A").lastName());

    assertEquals("a", Name.name("a").canonical());
    assertEquals("a", Name.name("a").packageName());
    assertEquals("a", Name.name("a").lastName());

    assertEquals("a.b", Name.name("a", "b").canonical());
    assertEquals("a.b", Name.name("a", "b").packageName());
    assertEquals("b", Name.name("a", "b").lastName());

    assertEquals("a.b.C", Name.name("a", "b", "C").canonical());
    assertEquals("a.b", Name.name("a", "b", "C").packageName());
    assertEquals("C", Name.name("a", "b", "C").lastName());

    assertEquals("java.lang.Object", Name.name(Object.class).canonical());
    assertEquals("java.lang", Name.name(Object.class).packageName());
    assertEquals("Object", Name.name(Object.class).lastName());

    assertEquals("java.lang.Thread.State.NEW", Name.name(Thread.State.NEW).canonical());
    assertEquals("java.lang", Name.name(Thread.State.NEW).packageName());
    assertEquals("NEW", Name.name(Thread.State.NEW).lastName());
  }

  @Test
  void primitive() {
    assertEquals("boolean", Name.name(boolean.class).canonical());
    assertEquals("byte", Name.name(byte.class).canonical());
    assertEquals("char", Name.name(char.class).canonical());
    assertEquals("double", Name.name(double.class).canonical());
    assertEquals("float", Name.name(float.class).canonical());
    assertEquals("int", Name.name(int.class).canonical());
    assertEquals("long", Name.name(long.class).canonical());
    assertEquals("short", Name.name(short.class).canonical());
    assertEquals("void", Name.name(void.class).canonical());
  }

  @Test
  void string() {
    assertEquals("Name{/void}", Name.name(void.class).toString());
    assertEquals("Name{/int}", Name.name(int.class).toString());
    assertEquals("Name{/int[]}", Name.name(int[].class).toString());
    assertEquals("Name{/int[][]}", Name.name(int[][].class).toString());
    assertEquals("Name{java.lang/Thread.State[]}", Name.name(Thread.State[].class).toString());
    assertEquals("Name{java.lang/Thread.State.NEW}", Name.name(Thread.State.NEW).toString());
  }
}
