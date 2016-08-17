package com.github.sormuras.listing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.expectThrows;

import java.beans.Transient;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.math.BigInteger;
import javax.annotation.Generated;
import org.junit.jupiter.api.Test;
import test.All;
import test.IllegalAnnotation;

@All(
  o = @Target(ElementType.TYPE),
  p = 1701,
  f = 11.1,
  e = (float) Math.E,
  m = {9, 8, 1},
  l = Override.class,
  j = @Documented,
  q = @Transient(true),
  r = {Float.class, Double.class}
)
class AnnotationTest {

  @Test
  void illegalAnnotationFails() {
    Error error = expectThrows(AssertionError.class, () -> Annotation.of(new IllegalAnnotation()));
    assertTrue(error.getMessage().contains("IllegalAnnotation"));
  }

  @Test
  void reflect() {
    Annotation annotation = Annotation.of(getClass().getAnnotation(All.class));
    assertEquals(
        "@"
            + All.class.getCanonicalName()
            + "("
            + "e = 2.718282F, "
            + "f = 11.1, "
            + "l = java.lang.Override.class, "
            + "m = {9, 8, 1}, "
            + "o = @java.lang.annotation.Target(java.lang.annotation.ElementType.TYPE), "
            + "p = 1701, "
            + "q = @java.beans.Transient, "
            + "r = {java.lang.Float.class, java.lang.Double.class}"
            + ")",
        annotation.list());
    Annotation annodefaults = Annotation.of(getClass().getAnnotation(All.class), true);
    assertEquals(
        "@"
            + All.class.getCanonicalName()
            + "("
            + "a = 5, "
            + "b = 6, "
            + "c = 7, "
            + "d = 8L, "
            + "e = 2.718282F, "
            + "f = 11.1, "
            + "g = {'\\u0000', '쫾', 'z', '€', 'ℕ', '\"', '\\'', '\\t', '\\n'}, "
            + "h = true, "
            + "i = java.lang.Thread.State.BLOCKED, "
            + "j = @java.lang.annotation.Documented, "
            + "k = \"kk\", "
            + "l = java.lang.Override.class, "
            + "m = {9, 8, 1}, "
            + "n = {java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD}, "
            + "o = @java.lang.annotation.Target(java.lang.annotation.ElementType.TYPE), "
            + "p = 1701, "
            + "q = @java.beans.Transient, "
            + "r = {java.lang.Float.class, java.lang.Double.class}"
            + ")",
        annodefaults.list());
  }

  @Test
  void simpleMarkerAnnotation() {
    Annotation marker = Annotation.of(Test.class);
    assertEquals("@" + Test.class.getCanonicalName(), marker.list());
    assertEquals(
        "Annotation{Name{"
            + Test.class.getCanonicalName()
            + ", target=Optional[TYPE], modifiers=[public, abstract]}, members={}}",
        String.format("%s", marker));
  }

  @Test
  void singleElementAnnotation() {
    Class<Generated> type = Generated.class;
    Annotation tag = Annotation.of(type, "(-:");
    assertEquals("@" + type.getCanonicalName() + "(\"(-:\")", tag.list());
    Annotation tags = Annotation.of(type, "(", "-", ":");
    assertEquals("@" + type.getCanonicalName() + "({\"(\", \"-\", \":\"})", tags.list());
  }

  @Test
  void singleElementAnnotationUsingEnumValues() {
    Annotation target = Annotation.of(Target.class, ElementType.TYPE);
    String t = Target.class.getCanonicalName();
    String et = ElementType.class.getCanonicalName();
    assertEquals("@" + t + "(" + et + ".TYPE)", target.list());
    target.addObject("value", ElementType.PACKAGE);
    assertEquals("@" + t + "({" + et + ".TYPE, " + et + ".PACKAGE})", target.list());
  }

  @Test
  void singleElementNotNamedValue() {
    Annotation a = new Annotation(Name.of("a", "A"));
    a.addMember("a", Annotation.value("zzz"));
    assertEquals("@a.A(a = \"zzz\")", a.list());
    a.addMember("b", Annotation.value(4711));
    assertEquals("@a.A(a = \"zzz\", b = 4711)", a.list());
  }

  @Test
  void value() {
    assertEquals("int.class", Annotation.value(int.class).list());
    assertEquals("java.lang.Thread.State.class", Annotation.value(Thread.State.class).list());
    assertEquals("java.lang.Thread.State.NEW", Annotation.value(Thread.State.NEW).list());
    assertEquals("\"a\"", Annotation.value("a").list());
    assertEquals("2.718282F", Annotation.value((float) Math.E).list());
    assertEquals("2.718281828459045", Annotation.value(Math.E).list());
    assertEquals("9223372036854775807L", Annotation.value(Long.MAX_VALUE).list());
    assertEquals("'!'", Annotation.value('!').list());
    assertEquals("null", Annotation.value(null).list());
    assertEquals("0", Annotation.value(BigInteger.ZERO).list());
  }
}
