package com.github.sormuras.listing.type;

import static com.github.sormuras.listing.Tests.proxy;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.expectThrows;

import com.github.sormuras.listing.Annotatable;
import com.github.sormuras.listing.Annotation;
import com.github.sormuras.listing.Compilation;
import com.github.sormuras.listing.Tests;
import com.github.sormuras.listing.unit.Block;
import com.github.sormuras.listing.unit.ClassDeclaration;
import com.github.sormuras.listing.unit.CompilationUnit;
import com.github.sormuras.listing.unit.InterfaceDeclaration;
import com.github.sormuras.listing.unit.MethodDeclaration;
import com.github.sormuras.listing.unit.MethodParameter;
import com.github.sormuras.listing.unit.NormalClassDeclaration;
import com.github.sormuras.listing.unit.TypeParameter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.net.URI;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.tools.JavaFileObject;
import org.junit.jupiter.api.Test;
import test.All;

class JavaMirrorsTest {

  private <A extends Annotatable> A mark(A annotatable) {
    annotatable.addAnnotation(Counter.Mark.class);
    return annotatable;
  }

  private void primitives(Counter counter) {
    assertEquals(9, counter.map.size());
    assertEquals(JavaType.of(boolean.class), counter.map.get("field1"));
    assertEquals(JavaType.of(byte.class), counter.map.get("field2"));
    assertEquals(JavaType.of(char.class), counter.map.get("field3"));
    assertEquals(JavaType.of(double.class), counter.map.get("field4"));
    assertEquals(JavaType.of(float.class), counter.map.get("field5"));
    assertEquals(JavaType.of(int.class), counter.map.get("field6"));
    assertEquals(JavaType.of(long.class), counter.map.get("field7"));
    assertEquals(JavaType.of(short.class), counter.map.get("field8"));
    assertEquals(JavaType.of(void.class), counter.map.get("noop"));
  }

  @Test
  void primitivesFromCompilationUnit() throws Exception {
    CompilationUnit unit = CompilationUnit.of("test");
    ClassDeclaration type = unit.declareClass("PrimitiveFields");
    mark(type.declareField(boolean.class, "field1"));
    mark(type.declareField(byte.class, "field2"));
    mark(type.declareField(char.class, "field3"));
    mark(type.declareField(double.class, "field4"));
    mark(type.declareField(float.class, "field5"));
    mark(type.declareField(int.class, "field6"));
    mark(type.declareField(long.class, "field7"));
    mark(type.declareField(short.class, "field8"));
    MethodDeclaration noop = type.declareMethod(void.class, "noop");
    noop.setBody(new Block());
    noop.addAnnotation(Counter.Mark.class);

    Counter counter = new Counter();
    Compilation.compile(null, emptyList(), asList(counter), asList(unit.toJavaFileObject()));
    primitives(counter);
  }

  @Test
  void primitivesFromFile() {
    String charContent = Tests.load(JavaMirrorsTest.class, "primitives");
    JavaFileObject source = Compilation.source(URI.create("test/Primitives.java"), charContent);
    Counter counter = new Counter();
    Compilation.compile(getClass().getClassLoader(), emptyList(), asList(counter), asList(source));
    primitives(counter);
  }

  @Test
  void rootAnnotation() {
    CompilationUnit unit = CompilationUnit.of("test");

    Annotation annotation = Annotation.of(All.class);
    annotation.addObject("o", Annotation.of(Target.class, ElementType.TYPE));
    annotation.addObject("p", 4711);
    annotation.addObject("r", Double.class);
    annotation.addObject("r", Float.class);

    NormalClassDeclaration type = unit.declareClass("Root");
    type.addAnnotation(annotation);
    type.addTypeParameter(TypeParameter.of("X"));
    mark(type.declareField(TypeVariable.of("X"), "i"));

    Counter counter = new Counter();
    Compilation.compile(null, emptyList(), asList(counter), asList(unit.toJavaFileObject()));
    assertEquals(1, counter.annotations.size());
    assertEquals(annotation.list(), counter.annotations.get(0).list());
  }

  @Test
  void unknownTypeFails() {
    AssertionError e =
        expectThrows(
            AssertionError.class,
            () -> JavaMirrors.of(Tests.proxy(PrimitiveType.class, (p, m, a) -> TypeKind.ERROR)));
    assertTrue(e.toString().contains("Unsupported primitive type"));
    e =
        expectThrows(
            AssertionError.class,
            () -> JavaMirrors.of(Tests.proxy(NoType.class, (p, m, a) -> TypeKind.ERROR)));
    assertTrue(e.toString().contains("Unsupported no type"));
  }

  @Test
  void visitor() {
    JavaMirrors.Visitor visitor = new JavaMirrors.Visitor();
    NoType voidType = proxy(NoType.class, (p, m, a) -> TypeKind.VOID);
    assertEquals(JavaType.of(void.class), visitor.visitNoType(voidType, null));
  }

  @Test
  void errorType() {
    CompilationUnit unit = CompilationUnit.of("test");
    unit.declareClass("Root");
    Counter counter = new Counter();
    Compilation.compile(null, emptyList(), asList(counter), asList(unit.toJavaFileObject()));

    DeclaredType dt =
        counter.typeUtils.getDeclaredType(
            counter.elementUtils.getTypeElement(Byte.class.getCanonicalName()));
    ErrorType errorType = new DeclaredTypeAsErrorType(dt);
    JavaMirrors.Visitor visitor = new JavaMirrors.Visitor();
    assertEquals(JavaType.of(Byte.class), visitor.visitError(errorType, null));
  }

  @Test
  void intersectionType() {
    CompilationUnit unit = CompilationUnit.of("test");

    InterfaceDeclaration intersection = unit.declareInterface("Intersection");
    MethodDeclaration method = mark(intersection.declareMethod(void.class, "test"));
    method.addTypeParameter(
        TypeParameter.of("T", JavaType.of(Number.class), JavaType.of(Runnable.class)));
    method.addParameter(MethodParameter.of(TypeVariable.of("T"), "t"));

    Counter counter = new Counter();
    Compilation.compile(null, emptyList(), asList(counter), asList(unit.toJavaFileObject()));
    assertEquals(1, counter.map.size());
  }
}
