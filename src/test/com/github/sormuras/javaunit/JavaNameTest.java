package com.github.sormuras.javaunit;

import static com.github.sormuras.javaunit.JavaName.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.lang.annotation.ElementType;
import java.util.Optional;

import org.junit.Test;

public class JavaNameTest {

  @Test
  public void apply() {
    assertEquals("byte", of(byte.class).apply(new Listing()).toString());
  }

  @Test
  public void enclosed() {
    assertEquals(false, of(byte.class).isEnclosed());
    assertEquals(false, of(Object.class).isEnclosed());
    assertEquals(false, of(Thread.class).isEnclosed());
    assertEquals(true, of(Thread.State.class).isEnclosed());
  }

  @Test
  public void enclosing() {
    assertEquals(Optional.empty(), of(byte.class).getEnclosing());
    assertEquals(Optional.empty(), of(Object.class).getEnclosing());
    assertEquals(Optional.empty(), of(Thread.class).getEnclosing());
    assertEquals(of(Thread.class), of(Thread.State.class).getEnclosing().get());
  }

  @Test
  public void equalsAndHashcode() {
    assertEquals(of(byte.class), of("", "byte"));
    assertEquals(of(Object.class), of("java.lang", "Object"));
    assertEquals(of(Thread.class), of("java.lang", "Thread"));
    assertEquals(of(Thread.State.class), of("java.lang", "Thread", "State"));
    // single instance
    JavaName integer = of(int.class);
    assertEquals(integer, integer);
    // falsify
    assertFalse(of(byte.class).equals(null));
    assertFalse(of(byte.class).equals(byte.class));
    assertFalse(of(byte.class).equals(of("a", "byte")));
  }

  @Test
  public void simpleNamesJoined() {
    assertEquals("Object", simpleNamesJoined(of(Object.class)));
    assertEquals("byte", simpleNamesJoined(of(byte.class)));
    assertEquals("Object[]", simpleNamesJoined(of(Object[].class)));
    assertEquals("Object[][]", simpleNamesJoined(of(Object[][].class)));
    assertEquals(JavaName.class.getSimpleName(), simpleNamesJoined(of(JavaName.class)));
    assertEquals("Character.Subset", simpleNamesJoined(of(Character.Subset.class)));
    assertEquals("Thread.State", simpleNamesJoined(of(Thread.State.class)));
  }

  String simpleNamesJoined(JavaName name) {
    return String.join(".", name.getSimpleNames());
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidIdentifier() {
    of();
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidIdentifierName() {
    of("test", "123");
  }

  @Test
  public void isStatic() throws Exception {
    assertEquals(false, of(Object.class).isStatic());
    assertEquals(false, of(Object.class.getDeclaredConstructor()).isStatic());
    assertEquals(false, of(Object.class.getDeclaredMethod("toString")).isStatic());
    assertEquals(true, of(Object.class.getDeclaredMethod("registerNatives")).isStatic());
    assertEquals(false, of(Object.class.getDeclaredMethod("clone")).isStatic());
    assertEquals(false, of(Thread.class.getDeclaredMethod("start")).isStatic());
    assertEquals(true, of(Math.class.getDeclaredField("PI")).isStatic());
    assertEquals(false, of(byte.class).isStatic());
    assertEquals(true, of(Character.Subset.class).isStatic());
    assertEquals(true, of(Thread.State.class).isStatic());
    assertEquals(true, of(Thread.State.NEW).isStatic());
  }

  @Test
  public void list() {
    assertEquals("a.b.X", of("a.b", "X").list());
  }

  @Test
  public void target() {
    assertEquals(ElementType.PACKAGE, of("a.b").getTarget().get());
    assertEquals(ElementType.TYPE, of("a.b", "X").getTarget().get());
    assertEquals(false, of("a.b", "X", "member").getTarget().isPresent());
    assertEquals(ElementType.TYPE, of(Object.class).getTarget().get());
    assertEquals(ElementType.TYPE, of(byte.class).getTarget().get());
    assertEquals(ElementType.TYPE, of(Object[].class).getTarget().get());
    assertEquals(ElementType.TYPE, of(Object[][].class).getTarget().get());
    assertEquals(ElementType.TYPE, of(JavaName.class).getTarget().get());
    assertEquals(ElementType.TYPE, of(Character.Subset.class).getTarget().get());
    assertEquals(ElementType.TYPE, of(Thread.State.class).getTarget().get());
    assertEquals(ElementType.FIELD, of(Thread.State.NEW).getTarget().get());
  }

  @Test
  public void testToString() {
    assertEquals("JavaName{a.b.X, target=Optional[TYPE], modifiers=[]}", of("a.b", "X").toString());
    testToString(of(Object.class), "java.lang.Object", "public");
    testToString(of(byte.class), "byte", "public, abstract, final");
    testToString(of(Object[].class), "Object[]", "public, abstract, final");
    testToString(of(Object[][].class), "Object[][]", "public, abstract, final");
    testToString(of(JavaName.class), JavaName.class.getCanonicalName(), "public");
    testToString(of(Character.Subset.class), "java.lang.Character.Subset", "public, static");
    testToString(of(Thread.State.class), "java.lang.Thread.State", "public, static, final");
    testToString(
        of(Thread.State.NEW),
        "java.lang.Thread.State.NEW",
        ElementType.FIELD,
        "public, static, final");
  }

  void testToString(JavaName name, String canonical, String modifiers) {
    testToString(name, canonical, ElementType.TYPE, modifiers);
  }

  void testToString(JavaName name, String canonical, ElementType type, String modifiers) {
    assertEquals(
        "JavaName{" + canonical + ", target=Optional[" + type + "], modifiers=[" + modifiers + "]}",
        name.toString());
  }
}
