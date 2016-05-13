package com.github.sormuras.javaunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.AnnotatedType;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class JavaTypeTest<T> {

  int a = 4;

  @U int b = 4;

  int c @U [] @U [] @U [] = {};

  @U List<@U String> los = Collections.emptyList();

  List<@U T> lot = Collections.emptyList();

  List<@U ?> low = Collections.emptyList();

  List<@U ? extends T> lowe = Collections.emptyList();

  List<@U ? super T> lows = Collections.emptyList();

  private String asString(String fieldName) throws Exception {
    AnnotatedType annotatedType = getClass().getDeclaredField(fieldName).getAnnotatedType();
    Listing listing = new Listing();
    // listing.getImportDeclarations().add(U.class);
    // listing.getImportDeclarations().add(List.class);
    return listing.add(JavaType.of(annotatedType)).toString();
  }

  @Test
  public void voidType() {
    assertEquals("void", JavaType.of(void.class).list());
  }

  @Test
  public void reflectFieldType() throws Exception {
    assertEquals("int", asString("a"));
    assertEquals(U.USE + " int", asString("b"));
    assertEquals("int" + U.USE + " []" + U.USE + " []" + U.USE + " []", asString("c"));
    assertEquals("java.util." + U.USE + " List<java.lang." + U.USE + " String>", asString("los"));
    assertEquals("java.util.List<" + U.USE + " T>", asString("lot"));
    assertEquals("java.util.List<" + U.USE + " ?>", asString("low"));
    assertEquals("java.util.List<" + U.USE + " ? extends T>", asString("lowe"));
    assertEquals("java.util.List<" + U.USE + " ? super T>", asString("lows"));
  }

  @Test
  public void className() {
    assertEquals(boolean.class.getName(), JavaType.of(boolean.class).toClassName());
    assertEquals(byte.class.getName(), JavaType.of(byte.class).toClassName());
    assertEquals(char.class.getName(), JavaType.of(char.class).toClassName());
    assertEquals(double.class.getName(), JavaType.of(double.class).toClassName());
    assertEquals(float.class.getName(), JavaType.of(float.class).toClassName());
    assertEquals(int.class.getName(), JavaType.of(int.class).toClassName());
    assertEquals(long.class.getName(), JavaType.of(long.class).toClassName());
    assertEquals(short.class.getName(), JavaType.of(short.class).toClassName());
    assertEquals(void.class.getName(), JavaType.of(void.class).toClassName());
    assertEquals(Object.class.getName(), JavaType.of(Object.class).toClassName());
    assertEquals(Thread.class.getName(), JavaType.of(Thread.class).toClassName());
    assertEquals(Thread.State.class.getName(), JavaType.of(Thread.State.class).toClassName());
    assertEquals(Object[].class.getName(), JavaType.of(Object[].class).toClassName());
    assertEquals(Object[][].class.getName(), JavaType.of(Object[][].class).toClassName());
    assertEquals(boolean[][][].class.getName(), JavaType.of(boolean[][][].class).toClassName());
    assertEquals(byte[][][].class.getName(), JavaType.of(byte[][][].class).toClassName());
    assertEquals(char[][][].class.getName(), JavaType.of(char[][][].class).toClassName());
    assertEquals(double[][][].class.getName(), JavaType.of(double[][][].class).toClassName());
    assertEquals(float[][][].class.getName(), JavaType.of(float[][][].class).toClassName());
    assertEquals(int[][][].class.getName(), JavaType.of(int[][][].class).toClassName());
    assertEquals(long[][][].class.getName(), JavaType.of(long[][][].class).toClassName());
    assertEquals(short[][][].class.getName(), JavaType.of(short[][][].class).toClassName());
    try {
      new Wildcard().toClassName();
      fail();
    } catch (UnsupportedOperationException expected) {
      assertTrue(expected.toString().contains("does not support"));
    }
  }

  @Test
  public void classType() {
    assertEquals("boolean", JavaType.of(boolean.class).list());
    assertEquals("byte", JavaType.of(byte.class).list());
    assertEquals("char", JavaType.of(char.class).list());
    assertEquals("double", JavaType.of(double.class).list());
    assertEquals("float", JavaType.of(float.class).list());
    assertEquals("int", JavaType.of(int.class).list());
    assertEquals("long", JavaType.of(long.class).list());
    assertEquals("short", JavaType.of(short.class).list());
    JavaType<?> uint = JavaType.of(int.class).addAnnotation(U.class);
    assertEquals("@com.github.sormuras.javaunit.U int", uint.list());
  }

  public List<String> parameterizedFieldType;

  @Test
  public void of() {
    assertEquals("java.lang.Object", JavaType.of(Object.class).list());
  }

  @Test
  public void type() throws Exception {
    assertEquals(
        "void",
        JavaType.of(JavaTypeTest.class.getDeclaredMethod("type").getGenericReturnType()).list());
    try {
      assertEquals(
          "void",
          JavaType.of(
                  JavaTypeTest.class.getDeclaredField("parameterizedFieldType").getGenericType())
              .list());
    } catch (AssertionError e) {
      // expected
    }
  }
}
