package com.github.sormuras.listing.type;

import static com.github.sormuras.listing.Tests.proxy;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.expectThrows;

import com.github.sormuras.listing.Compilation;
import com.github.sormuras.listing.Tests;
import com.github.sormuras.listing.unit.ClassDeclaration;
import com.github.sormuras.listing.unit.CompilationUnit;
import java.net.URI;
import javax.lang.model.type.NoType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVisitor;
import javax.tools.JavaFileObject;
import org.junit.jupiter.api.Test;

class JavaMirrorsTest {

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
    CompilationUnit unit = new CompilationUnit("test");
    ClassDeclaration type = unit.declareClass("PrimitiveFields");
    type.declareField(boolean.class, "field1").addAnnotation(Counter.Mark.class);
    type.declareField(byte.class, "field2").addAnnotation(Counter.Mark.class);
    type.declareField(char.class, "field3").addAnnotation(Counter.Mark.class);
    type.declareField(double.class, "field4").addAnnotation(Counter.Mark.class);
    type.declareField(float.class, "field5").addAnnotation(Counter.Mark.class);
    type.declareField(int.class, "field6").addAnnotation(Counter.Mark.class);
    type.declareField(long.class, "field7").addAnnotation(Counter.Mark.class);
    type.declareField(short.class, "field8").addAnnotation(Counter.Mark.class);
    type.declareMethod(void.class, "noop")
        .setBody(l -> l.add("{}"))
        .addAnnotation(Counter.Mark.class);

    Counter counter = new Counter();
    Compilation.compile(null, emptyList(), asList(counter), asList(unit.toJavaFileObject()));
    primitives(counter);
  }

  @Test
  void primitivesFromFile() {
    String charContent = Tests.load(JavaMirrorsTest.class, "primitives");
    JavaFileObject source = Compilation.source(URI.create("test/Primitives.java"), charContent);
    Counter counter = new Counter();
    Compilation.compile(null, emptyList(), asList(counter), asList(source));
    primitives(counter);
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
    TypeVisitor<JavaType, ?> visitor = new JavaMirrors.Visitor();
    NoType voidType = proxy(NoType.class, (p, m, a) -> TypeKind.VOID);
    assertEquals(JavaType.of(void.class), visitor.visitNoType(voidType, null));
  }
}
