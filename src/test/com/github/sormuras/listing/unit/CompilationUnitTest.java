package com.github.sormuras.listing.unit;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.expectThrows;

import com.github.sormuras.listing.Annotation;
import com.github.sormuras.listing.Compilation;
import com.github.sormuras.listing.Name;
import com.github.sormuras.listing.Tests;
import com.github.sormuras.listing.Tool;
import com.github.sormuras.listing.type.ClassType;
import com.github.sormuras.listing.type.Counter;
import com.github.sormuras.listing.type.JavaType;
import com.github.sormuras.listing.type.TypeVariable;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Generated;
import javax.lang.model.element.Modifier;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

class CompilationUnitTest {

  @Test
  void enterprise() {
    CompilationUnit unit = new CompilationUnit("uss");
    NormalClassDeclaration enterprise = unit.declareClass("Enterprise");
    enterprise.addModifier(Modifier.PUBLIC);
    enterprise.addInterface(ClassType.of(Supplier.class, String.class));
    enterprise.declareField(String.class, "text").addModifier("private", "final");
    enterprise.declareField(Number.class, "number").addModifier("private", "final");
    MethodDeclaration constructor = enterprise.declareConstructor();
    constructor.addModifier(Modifier.PUBLIC);
    constructor.addParameter(String.class, "text");
    constructor.addParameter(Number.class, "number");
    constructor.addStatement("this.text = text");
    constructor.addStatement("this.number = number");
    MethodDeclaration getter = enterprise.declareMethod(String.class, "get");
    getter.addAnnotation(Override.class);
    getter.addModifier(Modifier.PUBLIC);
    getter.addStatement("return text + '-' + number");

    Supplier<?> spaceship = unit.compile(Supplier.class, "NCC", (short) 1701);

    assertEquals("Enterprise", spaceship.getClass().getSimpleName());
    assertEquals("NCC-1701", spaceship.get());
    Tests.assertEquals(getClass(), "enterprise", unit);
  }

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
    unit.declareAnnotation("A").declareAnnotation("X");
    unit.declareEnum("E").declareEnum("X");
    unit.declareClass("C").declareClass("X").declareClass("Z");
    unit.declareInterface("I").declareInterface("X");
    assertEquals(4, unit.getDeclarations().size());
    assertFalse(unit.getDeclarations().get(0).isEmpty()); // A
    assertTrue(unit.getDeclarations().get(0).getDeclarations().get(0).isEmpty()); // A.X
    assertEquals(Tests.load(CompilationUnitTest.class, "top"), unit.list());
  }

  @Test
  void simple() {
    assertEquals(Tests.load(Units.class, "simple"), Units.simple().list());
  }

  @Test
  void processed() throws Exception {
    CompilationUnit unit = new CompilationUnit("test");
    ClassDeclaration enterprise = unit.declareClass("Class");
    enterprise.addModifier(Modifier.PUBLIC);
    enterprise.declareField(Object.class, "field1").addAnnotation(Counter.Mark.class);
    enterprise.declareField(Object.class, "field2").addAnnotation(Counter.Mark.class);
    Tests.assertEquals(getClass(), "processed", unit);

    Counter counter = new Counter();
    Compilation.compile(null, emptyList(), asList(counter), asList(unit.toJavaFileObject()));
    assertEquals(2, counter.listOfElements.size());
  }

  @Test
  void unnamed() throws Exception {
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

  @Test
  void crazy() throws Exception {
    Annotation tag = new Annotation(Name.of("", "Tag"));
    ClassType taggedRunnable = ClassType.of(Runnable.class);
    taggedRunnable.addAnnotation(tag);
    ClassType taggedString = ClassType.of(String.class);
    taggedString.addAnnotation(tag);
    ClassType taggedThread = ClassType.of(Thread.class);
    taggedThread.addAnnotation(tag);
    ClassType listOfStrings = ClassType.of(List.class, String.class);

    CompilationUnit unit = new CompilationUnit("abc.xyz");
    unit.getPackageDeclaration()
        .addAnnotation(Generated.class, "https://", "github.com/sormuras/listing");
    unit.getImportDeclarations()
        .addSingleTypeImport(Assert.class)
        .addTypeImportOnDemand(Name.of("abc"))
        .addSingleStaticImport(Name.of(Collections.class.getMethod("shuffle", List.class)))
        .addStaticImportOnDemand(Name.of(Objects.class));
    unit.declareAnnotation("TestAnno");
    EnumDeclaration e1 = unit.declareEnum("TestEnum");
    e1.addAnnotation(Generated.class, "An enum for testing");
    e1.addModifier(Modifier.PROTECTED);
    e1.addInterface(JavaType.of(Serializable.class));
    // TODO e1.setBody(l -> l.add("A, B, C").newline());
    unit.declareInterface("TestIntf");
    NormalClassDeclaration simple = unit.declareClass("SimpleClass");
    simple.addModifier("public", "final");
    simple.addTypeParameter(new TypeParameter("S").addBounds(taggedRunnable));
    simple.addTypeParameter(new TypeParameter("T").setBoundTypeVariable("S"));
    simple.setSuperClass(taggedThread);
    simple.addInterface(JavaType.of(Cloneable.class));
    simple.addInterface(JavaType.of(Runnable.class));
    FieldDeclaration i = simple.declareField(int.class, "i");
    i.addModifier("private", "volatile");
    i.setInitializer(l -> l.add("4711"));
    simple
        .declareField(taggedString, "s")
        .setInitializer(l -> l.add(Tool.escape("The Story about \"Ping\"")));
    simple
        .declareField(listOfStrings, "l")
        .setInitializer(l -> l.add("java.util.Collections.emptyList()"));
    MethodDeclaration run = simple.declareMethod(void.class, "run");
    run.addAnnotation(Override.class);
    run.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
    run.setBody(l -> l.add("System.out.println(\"Hallo Welt!\");").newline());
    MethodDeclaration calc = simple.declareMethod(new TypeVariable("N"), "calc");
    calc.addModifier(Modifier.STATIC);
    calc.addTypeParameter(new TypeParameter("N").addBounds(JavaType.of(Number.class)));
    calc.addParameter(int.class, "i");
    calc.addThrows(Exception.class);
    calc.setBody(l -> l.add("return null;").newline());
    // TODO simple.declareEnum("Innum").setBody(l -> l.add("X, Y, Z").newline());
    // TODO simple.declareClass("Cinner").setBody(l -> l.add("// empty").newline());

    assertSame(simple, i.getEnclosing().get());
    Tests.assertEquals(getClass(), "crazy", unit);
  }
}
