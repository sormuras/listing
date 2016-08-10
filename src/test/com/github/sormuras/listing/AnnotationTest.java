package com.github.sormuras.listing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.beans.Transient;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Native;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigInteger;
import javax.annotation.Generated;
import org.junit.jupiter.api.Test;

@Retention(RetentionPolicy.RUNTIME)
@interface Crazy {

  byte a() default 5;

  short b() default 6;

  int c() default 7;

  long d() default 8;

  float e() default 9.0f;

  double f() default 10.0;

  char[] g() default {0, 0xCAFE, 'z', '€', 'ℕ', '"', '\'', '\t', '\n'};

  boolean h() default true;

  Thread.State i() default Thread.State.BLOCKED;

  Documented j() default @Documented;

  String k() default "kk";

  Class<? extends java.lang.annotation.Annotation> l() default Native.class;

  int[] m() default {1, 2, 3};

  ElementType[] n() default {ElementType.FIELD, ElementType.METHOD};

  Target o();

  int p();

  Transient q() default @Transient(value = false);

  Class<? extends Number>[] r() default {Byte.class, Short.class, Integer.class, Long.class};
}

class IllegalAnnotation implements java.lang.annotation.Annotation {
  public void fail() {
    throw new AssertionError("illegal annotation implementation is illegal");
  }

  @Override
  public Class<? extends java.lang.annotation.Annotation> annotationType() {
    return IllegalAnnotation.class;
  }
}

@Crazy(
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
    try {
      Annotation.of(new IllegalAnnotation());
      fail("reflected illegal annotation implementation?!");
    } catch (AssertionError e) {
      assertEquals(true, e.toString().contains("IllegalAnnotation"));
    }
  }

  @Test
  void reflect() {
    Annotation annotation = Annotation.of(getClass().getAnnotation(Crazy.class));
    assertEquals(
        "@"
            + Crazy.class.getCanonicalName()
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
    Annotation annodefaults = Annotation.of(getClass().getAnnotation(Crazy.class), true);
    assertEquals(
        "@"
            + Crazy.class.getCanonicalName()
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
    assertEquals("9223372036854775807L", Annotation.value(Long.MAX_VALUE).list());
    assertEquals("'!'", Annotation.value('!').list());
    assertEquals("null", Annotation.value(null).list());
    assertEquals("0", Annotation.value(BigInteger.ZERO).list());
  }
}
