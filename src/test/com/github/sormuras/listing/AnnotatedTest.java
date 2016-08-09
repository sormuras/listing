package com.github.sormuras.listing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sormuras.listing.type.ArrayDimension;
import com.github.sormuras.listing.type.ArrayType;
import com.github.sormuras.listing.type.ClassName;
import com.github.sormuras.listing.type.ClassType;
import com.github.sormuras.listing.type.PrimitiveType;
import com.github.sormuras.listing.type.TypeVariable;
import com.github.sormuras.listing.type.U;
import com.github.sormuras.listing.type.V;
import com.github.sormuras.listing.type.VoidType;
import com.github.sormuras.listing.type.WildcardType;
import com.github.sormuras.listing.unit.AnnotationDeclaration;
import com.github.sormuras.listing.unit.AnnotationElement;
import com.github.sormuras.listing.unit.ConstantDeclaration;
import com.github.sormuras.listing.unit.EnumConstant;
import com.github.sormuras.listing.unit.EnumDeclaration;
import com.github.sormuras.listing.unit.FieldDeclaration;
import com.github.sormuras.listing.unit.InterfaceDeclaration;
import com.github.sormuras.listing.unit.MethodDeclaration;
import com.github.sormuras.listing.unit.MethodParameter;
import com.github.sormuras.listing.unit.NormalClassDeclaration;
import com.github.sormuras.listing.unit.PackageDeclaration;
import com.github.sormuras.listing.unit.TypeParameter;
import java.util.Collections;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class AnnotatedTest {

  @Test
  void annotatableAnnotationDeclaration() {
    test(AnnotationDeclaration::new);
  }

  @Test
  void annotatableAnnotationElement() {
    test(AnnotationElement::new);
  }

  @Test
  void annotatableArrayDimension() {
    test(ArrayDimension::new);
  }

  @Test
  void annotatableArrayType() {
    test(() -> ArrayType.of(int.class, 1));
  }

  @Test
  void annotatableClassName() {
    test(ClassName::new);
  }

  @Test
  void annotatableClassType() {
    test(() -> ClassType.of("pack.age", "ClassType"));
  }

  @Test
  void annotatableConstantDeclaration() {
    test(ConstantDeclaration::new);
  }

  @Test
  void annotatableEnumConstant() {
    test(EnumConstant::new);
  }

  @Test
  void annotatableEnumDeclaration() {
    test(EnumDeclaration::new);
  }

  @Test
  void annotatableFieldDeclaration() {
    test(FieldDeclaration::new);
  }

  @Test
  void annotatableInterfaceDeclaration() {
    test(InterfaceDeclaration::new);
  }

  @Test
  void annotatableMethodDeclaration() {
    test(MethodDeclaration::new);
  }

  @Test
  void annotatableMethodParameter() {
    test(MethodParameter::new);
  }

  @Test
  void annotatableNormalClassDeclaration() {
    test(NormalClassDeclaration::new);
  }

  @Test
  void annotatablePackageDeclaration() {
    test(PackageDeclaration::new);
  }

  @Test
  void annotatablePrimitiveTypes() {
    test(PrimitiveType.BooleanType::new);
    test(PrimitiveType.ByteType::new);
    test(PrimitiveType.CharType::new);
    test(PrimitiveType.DoubleType::new);
    test(PrimitiveType.FloatType::new);
    test(PrimitiveType.IntType::new);
    test(PrimitiveType.LongType::new);
    test(PrimitiveType.ShortType::new);
  }

  @Test
  void annotatableTypeParameter() {
    test(TypeParameter::new);
  }

  @Test
  void annotatableTypeVariable() {
    test(TypeVariable::new);
  }

  @Test
  void annotatableVoidType() {
    testInitial(new VoidType());
  }

  @Test
  void annotatableWildcardType() {
    test(WildcardType::new);
  }

  private void test(Supplier<? extends Annotated> supplier) {
    testInitial(supplier.get());
    testMutable(supplier.get());
  }

  private void testInitial(Annotated a) {
    assertFalse(a.isAnnotated());
    assertTrue(a.getAnnotations().isEmpty());
  }

  private void testMutable(Annotated a) {
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
