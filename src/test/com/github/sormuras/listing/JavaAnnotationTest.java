package com.github.sormuras.listing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.beans.Transient;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import javax.annotation.Generated;

import org.junit.Test;

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
public class JavaAnnotationTest {

  class IllegalAnnotation implements Annotation {
    public void fail() {
      throw new AssertionError("illegal annotation implementation is illegal");
    }

    @Override
    public Class<? extends Annotation> annotationType() {
      return IllegalAnnotation.class;
    }
  }

  @Test
  public void illegalAnnotationFails() {
    try {
      JavaAnnotation.of(new IllegalAnnotation());
      fail("reflected illegal annotation implementation?!");
    } catch (AssertionError e) {
      assertEquals(true, e.toString().contains("IllegalAnnotation"));
    }
  }

  @Test
  public void reflect() {
    JavaAnnotation annotation = JavaAnnotation.of(getClass().getAnnotation(Crazy.class));
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
    JavaAnnotation annodefaults = JavaAnnotation.of(getClass().getAnnotation(Crazy.class), true);
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
  public void simpleMarkerAnnotation() {
    JavaAnnotation marker = JavaAnnotation.of(Test.class);
    assertEquals("@" + Test.class.getCanonicalName(), marker.list());
    assertEquals(
        "JavaAnnotation{JavaName{"
            + Test.class.getCanonicalName()
            + ", target=Optional[TYPE], modifiers=[public, abstract]}, members={}}",
        String.format("%s", marker));
  }

  @Test
  public void singleElementAnnotation() {
    Class<Generated> type = Generated.class;
    JavaAnnotation tag = JavaAnnotation.of(type, "(-:");
    assertEquals("@" + type.getCanonicalName() + "(\"(-:\")", tag.list());
    JavaAnnotation tags = JavaAnnotation.of(type, "(", "-", ":");
    assertEquals("@" + type.getCanonicalName() + "({\"(\", \"-\", \":\"})", tags.list());
  }

  @Test
  public void singleElementAnnotationUsingEnumValues() {
    JavaAnnotation target = JavaAnnotation.of(Target.class, ElementType.TYPE);
    String t = Target.class.getCanonicalName();
    String et = ElementType.class.getCanonicalName();
    assertEquals("@" + t + "(" + et + ".TYPE)", target.list());
    target.addObject("value", ElementType.PACKAGE);
    assertEquals("@" + t + "({" + et + ".TYPE, " + et + ".PACKAGE})", target.list());
  }

  @Test
  public void singleElementNotNamedValue() {
    JavaAnnotation a = new JavaAnnotation(JavaName.of("a.A"));
    a.addMember("a", Listable.of("zzz"));
    assertEquals("@a.A(a = \"zzz\")", a.list());
    a.addMember("b", Listable.of(4711));
    assertEquals("@a.A(a = \"zzz\", b = 4711)", a.list());
  }
}
