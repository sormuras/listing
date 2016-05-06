package com.github.sormuras.javaunit;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ToolTest {

  @Test
  public void escapeCharacter() {
    assertEquals("a", Tool.escape('a'));
    assertEquals("b", Tool.escape('b'));
    assertEquals("c", Tool.escape('c'));
    assertEquals("%", Tool.escape('%'));
    // common escapes
    assertEquals("\\b", Tool.escape('\b'));
    assertEquals("\\t", Tool.escape('\t'));
    assertEquals("\\n", Tool.escape('\n'));
    assertEquals("\\f", Tool.escape('\f'));
    assertEquals("\\r", Tool.escape('\r'));
    assertEquals("\"", Tool.escape('"'));
    assertEquals("\\'", Tool.escape('\''));
    assertEquals("\\\\", Tool.escape('\\'));
    // octal escapes
    assertEquals("\\u0000", Tool.escape('\0'));
    assertEquals("\\u0007", Tool.escape('\7'));
    assertEquals("?", Tool.escape('\77'));
    assertEquals("\\u007f", Tool.escape('\177'));
    assertEquals("¿", Tool.escape('\277'));
    assertEquals("ÿ", Tool.escape('\377'));
    // unicode escapes
    assertEquals("\\u0000", Tool.escape('\u0000'));
    assertEquals("\\u0001", Tool.escape('\u0001'));
    assertEquals("\\u0002", Tool.escape('\u0002'));
    assertEquals("€", Tool.escape('\u20AC'));
    assertEquals("☃", Tool.escape('\u2603'));
    assertEquals("♠", Tool.escape('\u2660'));
    assertEquals("♣", Tool.escape('\u2663'));
    assertEquals("♥", Tool.escape('\u2665'));
    assertEquals("♦", Tool.escape('\u2666'));
    assertEquals("✵", Tool.escape('\u2735'));
    assertEquals("✺", Tool.escape('\u273A'));
    assertEquals("／", Tool.escape('\uFF0F'));
  }

  @Test
  public void escapeString() {
    escapeString("abc");
    escapeString("♦♥♠♣");
    escapeString("€\\t@\\t$", "€\t@\t$");
    escapeString("abc();\\ndef();", "abc();\ndef();");
    escapeString("This is \\\"quoted\\\"'!'", "This is \"quoted\"'!'");
    escapeString("e^{i\\\\pi}+1=0", "e^{i\\pi}+1=0");
  }

  void escapeString(String string) {
    escapeString(string, string);
  }

  void escapeString(String expected, String value) {
    assertEquals("\"" + expected + "\"", Tool.escape(value));
  }

  @Test
  public void packageOf() {
    assertEquals("", Tool.packageOf("Abc"));
    assertEquals("java.lang", Tool.packageOf("java.lang.Object"));
    assertEquals("java.lang", Tool.packageOf(Object.class));
    assertEquals("", Tool.packageOf(byte.class));
    assertEquals("", Tool.packageOf(Object[].class));
    assertEquals("", Tool.packageOf(Object[][].class));
    String thisPackageName = getClass().getPackage().getName();
    assertEquals(thisPackageName, Tool.packageOf(ToolTest.class));
    assertEquals(thisPackageName, Tool.packageOf(new Object() {}.getClass()));
  }

  @Test
  public void simpleNames() {
    assertEquals(asList("Object"), Tool.simpleNames(Object.class));
    assertEquals(asList("byte"), Tool.simpleNames(byte.class));
    assertEquals(asList("Object[]"), Tool.simpleNames(Object[].class));
    assertEquals(asList("Object[][]"), Tool.simpleNames(Object[][].class));
    assertEquals(asList(JavaName.class.getSimpleName()), Tool.simpleNames(JavaName.class));
    assertEquals(asList("Character", "Subset"), Tool.simpleNames(Character.Subset.class));
    assertEquals(asList("Thread", "State"), Tool.simpleNames(Thread.State.class));
  }
}
