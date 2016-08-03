package com.github.sormuras.listing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sormuras.listing.type.ArrayDimension;
import com.github.sormuras.listing.type.ArrayType;
import com.github.sormuras.listing.type.ClassName;
import com.github.sormuras.listing.type.ClassType;
import com.github.sormuras.listing.type.JavaType;
import com.github.sormuras.listing.type.PrimitiveType;
import com.github.sormuras.listing.type.TypeVariable;
import com.github.sormuras.listing.type.U;
import com.github.sormuras.listing.type.V;
import com.github.sormuras.listing.type.VoidType;
import com.github.sormuras.listing.type.WildcardType;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class AnnotatableTest {

  @Test
  void annotatableArrayDimension() {
    test(new ArrayDimension());
  }

  @Test
  void annotatableArrayType() {
    test(new ArrayType(JavaType.of(int.class), 1));
  }

  @Test
  void annotatableClassName() {
    test(new ClassName("ClassName"));
  }

  @Test
  void annotatableClassType() {
    test(new ClassType(Name.of("pack.age", "ClassType")));
  }

  @Test
  void annotatablePrimitiveType() {
    test(new PrimitiveType(int.class));
  }

  @Test
  void annotatableTypeVariable() {
    test(new TypeVariable("TYPEVARIABLE"));
  }

  @Test
  void annotatableVoidType() {
    testInitial(new VoidType());
  }

  @Test
  void annotatableWildcardType() {
    test(new WildcardType());
  }

  private void test(Annotatable a) {
    testInitial(a);
    testMutable(a);
  }

  private void testInitial(Annotatable a) {
    assertFalse(a.isAnnotated());
    assertTrue(a.getAnnotations().isEmpty());
  }

  private void testMutable(Annotatable a) {
    // first non-readonly access initializes annotation collection
    assertFalse(a.isAnnotated());
    assertTrue(Collections.EMPTY_LIST != a.getAnnotations());
    assertFalse(a.isAnnotated());
    a.addAnnotation(U.class);
    assertTrue(a.isAnnotated());
    assertEquals(1, a.getAnnotations().size());
    a.setAnnotations();
    assertFalse(a.isAnnotated());
    a.addAnnotations(Annotation.of(U.class), Annotation.of(V.class));
    assertTrue(a.isAnnotated());
    assertEquals(2, a.getAnnotations().size());
  }
}
