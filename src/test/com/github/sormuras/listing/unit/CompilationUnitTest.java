package com.github.sormuras.listing.unit;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.expectThrows;

import com.github.sormuras.listing.Annotation;
import com.github.sormuras.listing.Compilation;
import com.github.sormuras.listing.Tests;
import com.github.sormuras.listing.type.Counter;
import java.util.function.Supplier;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Test;

class CompilationUnitTest {

  @Test
  void packageName() {
    assertEquals("", new CompilationUnit().getPackageName());
    assertEquals("a.b.c", new CompilationUnit("a.b.c").getPackageName());
  }

  @Test
  void eponymousDeclaration() {
    // empty unit
    assertFalse(new CompilationUnit().getEponymousDeclaration().isPresent());
    // single type in unit
    CompilationUnit singleTypeUnit = new CompilationUnit();
    singleTypeUnit.declareClass("NonPublic");
    assertTrue(singleTypeUnit.getEponymousDeclaration().isPresent());
    assertEquals("NonPublic", singleTypeUnit.getEponymousDeclaration().get().getName());
    // multiple top level classes
    assertEquals("Gamma", Units.simple().getEponymousDeclaration().get().getName());
  }

  @Test
  void top() {
    assertEquals(3, Units.simple().getDeclarations().size());
    CompilationUnit unit = new CompilationUnit("top");
    unit.declareAnnotation("A").declareClass("X");
    unit.declareEnum("E").declareClass("X");
    unit.declareClass("C").declareClass("X");
    unit.declareInterface("I").declareClass("X");
    assertEquals(4, unit.getDeclarations().size());
    assertEquals(Tests.load(CompilationUnitTest.class, "top"), unit.list());
  }

  @Test
  void simple() {
    assertEquals(Tests.load(Units.class, "simple"), Units.simple().list());
  }

  @Test
  public void processed() throws Exception {
    CompilationUnit unit = new CompilationUnit("test");
    ClassDeclaration enterprise = unit.declareClass("Class");
    enterprise.addModifier(Modifier.PUBLIC);
    enterprise
        .declareField(Object.class, "field1")
        .addAnnotation(Annotation.of(Counter.Mark.class));
    enterprise
        .declareField(Object.class, "field2")
        .addAnnotation(Annotation.of(Counter.Mark.class));
    Tests.assertEquals(getClass(), "processed", unit);

    Counter counter = new Counter();
    Compilation.compile(null, emptyList(), asList(counter), asList(unit.toJavaFileObject()));
    assertEquals(2, counter.listOfElements.size());
  }

  @Test
  public void unnamed() throws Exception {
    CompilationUnit unnamed = new CompilationUnit(new PackageDeclaration());
    unnamed.declareClass("Unnamed").addModifier(Modifier.PUBLIC);
    assertEquals("Unnamed", unnamed.compile(Object.class).getClass().getTypeName());
    expectThrows(Error.class, () -> unnamed.compile(Object.class, "unused", "arguments"));
    // with types supplier...
    Supplier<Class<?>[]> types0 = () -> new Class<?>[0];
    assertEquals("Unnamed", unnamed.compile(Object.class, types0).getClass().getTypeName());
    Supplier<Class<?>[]> types1 = () -> new Class<?>[1];
    expectThrows(Error.class, () -> unnamed.compile(Object.class, types1));
  }
}
