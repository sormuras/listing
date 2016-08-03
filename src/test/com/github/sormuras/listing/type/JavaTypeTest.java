package com.github.sormuras.listing.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.sormuras.listing.Listing;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class JavaTypeTest<T> {

  int a = 4;

  @U int b = 4;

  int c @U [] @U [] @U [] = {};

  List<String> @U [] @U [] d = null;

  @U List<@U String> los = Collections.emptyList();

  List<@U T> lot = Collections.emptyList();

  List<@U ?> low = Collections.emptyList();

  List<@U ? extends T> lowe = Collections.emptyList();

  List<@U ? super T> lows = Collections.emptyList();

  private String asAnno(String fieldName) throws Exception {
    AnnotatedType annotatedType = getClass().getDeclaredField(fieldName).getAnnotatedType();
    Listing listing = new Listing();
    // listing.getImportDeclarations().add(U.class);
    // listing.getImportDeclarations().add(List.class);
    return listing.add(JavaType.of(annotatedType)).toString();
  }

  private String asGenericType(String fieldName) throws Exception {
    Type type = getClass().getDeclaredField(fieldName).getGenericType();
    Listing listing = new Listing();
    // listing.getImportDeclarations().add(U.class);
    // listing.getImportDeclarations().add(List.class);
    return listing.add(JavaType.of(type)).toString();
  }

  @Test
  void voidType() {
    assertEquals("void", JavaType.of(void.class).list());
  }

  @Test
  void reflectFieldTypeAsAnnotatedType() throws Exception {
    assertEquals("int", asAnno("a"));
    assertEquals(U.USE + " int", asAnno("b"));
    assertEquals("int" + U.USE + " []" + U.USE + " []" + U.USE + " []", asAnno("c"));
    assertEquals("java.util.List<java.lang.String>" + U.USE + " []" + U.USE + " []", asAnno("d"));
    assertEquals("java.util." + U.USE + " List<java.lang." + U.USE + " String>", asAnno("los"));
    assertEquals("java.util.List<" + U.USE + " T>", asAnno("lot"));
    assertEquals("java.util.List<" + U.USE + " ?>", asAnno("low"));
    assertEquals("java.util.List<" + U.USE + " ? extends T>", asAnno("lowe"));
    assertEquals("java.util.List<" + U.USE + " ? super T>", asAnno("lows"));
  }

  @Test
  void reflectFieldTypeAsGenericType() throws Exception {
    assertEquals("int", asGenericType("a"));
    assertEquals("int", asGenericType("b"));
    assertEquals("int[][][]", asGenericType("c"));
    assertEquals("java.util.List<java.lang.String>[][]", asGenericType("d"));
    assertEquals("java.util.List<java.lang.String>", asGenericType("los"));
    assertEquals("java.util.List<T>", asGenericType("lot"));
    assertEquals("java.util.List<?>", asGenericType("low"));
    assertEquals("java.util.List<? extends T>", asGenericType("lowe"));
    assertEquals("java.util.List<? super T>", asGenericType("lows"));
  }

  @Test
  void className() {
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
    assertThrows(UnsupportedOperationException.class, () -> new WildcardType().toClassName());
  }

  @Test
  void classType() {
    assertEquals("boolean", JavaType.of(boolean.class).list());
    assertEquals("byte", JavaType.of(byte.class).list());
    assertEquals("char", JavaType.of(char.class).list());
    assertEquals("double", JavaType.of(double.class).list());
    assertEquals("float", JavaType.of(float.class).list());
    assertEquals("int", JavaType.of(int.class).list());
    assertEquals("long", JavaType.of(long.class).list());
    assertEquals("short", JavaType.of(short.class).list());
    JavaType uint = JavaType.of(int.class);
    uint.addAnnotation(U.class);
    assertEquals(U.USE + " int", uint.list());
  }

  public List<String> parameterizedFieldType;

  @Test
  void of() {
    assertEquals("java.lang.Object", JavaType.of(Object.class).list());
  }

  @Test
  void type() throws Exception {
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
