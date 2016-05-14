package com.github.sormuras.listing;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;

import org.junit.Assert;
import org.junit.Test;

import com.github.sormuras.listing.ClassDeclaration;
import com.github.sormuras.listing.ClassType;
import com.github.sormuras.listing.Compilation;
import com.github.sormuras.listing.FieldDeclaration;
import com.github.sormuras.listing.JavaAnnotation;
import com.github.sormuras.listing.JavaName;
import com.github.sormuras.listing.JavaType;
import com.github.sormuras.listing.JavaUnit;
import com.github.sormuras.listing.Listable;
import com.github.sormuras.listing.PackageDeclaration;
import com.github.sormuras.listing.TypeParameter;
import com.github.sormuras.listing.TypeVariable;

public class JavaUnitTest {

  @Test
  public void compile() throws Exception {
    JavaUnit unit = new JavaUnit("abc.def");
    unit.declareClass("Xyz").addModifier("public");
    Class<?> type = unit.compile();
    assertEquals("abc.def.Xyz", type.getTypeName());
    assertEquals("Xyz", type.newInstance().getClass().getSimpleName());
    unit.declareClass("Yzx");
    type = unit.compile();
    assertEquals("abc.def.Xyz", type.getTypeName());
    assertEquals("Xyz", type.newInstance().getClass().getSimpleName());
  }

  @Test
  public void crazy() throws Exception {
    JavaAnnotation tag = new JavaAnnotation(JavaName.of("", "Tag"));
    ClassType listOfStrings = ClassType.of(List.class, String.class);

    JavaUnit unit = new JavaUnit("abc.xyz");
    unit.getPackageDeclaration()
        .addAnnotation(Generated.class, "https://", "github.com/sormuras/listing");
    unit.getImportDeclarations()
        .addSingleTypeImport(Assert.class)
        .addTypeImportOnDemand(JavaName.of("abc"))
        .addSingleStaticImport(JavaName.of(Collections.class.getMethod("shuffle", List.class)))
        .addStaticImportOnDemand(JavaName.of(Objects.class));
    unit.declareAnnotation("TestAnno");
    unit.declareEnum("TestEnum")
        .addAnnotation(Generated.class, "An enum for testing")
        .addModifier(Modifier.PROTECTED)
        .addInterface(JavaType.of(Serializable.class))
        .setBody(l -> l.add("A, B, C").newline());
    unit.declareInterface("TestIntf");
    ClassDeclaration simple =
        unit.declareClass("SimpleClass")
            .addModifier("public", "final")
            .addTypeParameter(
                new TypeParameter("S").addBounds(JavaType.of(Runnable.class).addAnnotation(tag)))
            .addTypeParameter(new TypeParameter("T").setBoundTypeVariable("S"))
            .setSuperClass(JavaType.of(Thread.class).addAnnotation(tag))
            .addInterface(JavaType.of(Cloneable.class))
            .addInterface(JavaType.of(Runnable.class));
    FieldDeclaration i =
        simple
            .addFieldDeclaration(int.class, "i")
            .addModifier("private", "volatile")
            .setInitializer(Listable.of(4711));
    simple
        .addFieldDeclaration(JavaType.of(String.class).addAnnotation(tag), "s")
        .setInitializer(Listable.of("The Story about \"Ping\""));
    simple
        .addFieldDeclaration(listOfStrings, "l")
        .setInitializer(l -> l.add("java.util.Collections.emptyList()"));
    simple
        .addMethodDeclaration(void.class, "run")
        .addAnnotation(Override.class)
        .addModifier(Modifier.PUBLIC, Modifier.FINAL)
        .setBody(l -> l.add("System.out.println(\"Hallo Welt!\");").newline());
    simple
        .addMethodDeclaration(new TypeVariable().setName("N"), "calc")
        .addModifier(Modifier.STATIC)
        .addTypeParameter(new TypeParameter("N").addBounds(JavaType.of(Number.class)))
        .addParameter(int.class, "i")
        .addThrows(Exception.class)
        .setBody(l -> l.add("return null;").newline());
    simple.declareEnum("Innum").setBody(l -> l.add("X, Y, Z").newline());
    simple.declareClass("Cinner").setBody(l -> l.add("// empty").newline());

    assertSame(simple, i.getEnclosingType().get());
    Text.assertEquals(getClass(), "crazy", unit);
  }

  @Test
  public void duplicateClassFails() {
    JavaUnit unit = new JavaUnit("test");
    unit.declareInterface("I");
    try {
      Compilation.compile(getClass().getClassLoader(), emptyList(), emptyList(), unit, unit);
      fail();
    } catch (Exception e) {
      assertEquals(true, e.toString().contains("duplicate class"));
    }
  }

  @Test
  public void nonExistingAnnotationClassFails() {
    JavaUnit unit = new JavaUnit("test");
    unit.declareInterface("I").addAnnotation("does.not", "Exist");
    try {
      Compilation.compile(getClass().getClassLoader(), emptyList(), emptyList(), unit);
      fail();
    } catch (Exception e) {
      assertEquals(true, e.toString().contains("does not exist"));
    }
  }

  @Test
  public void loadUnknownFails() {
    JavaUnit unit = new JavaUnit("test");
    unit.declareInterface("I");
    ClassLoader l = Compilation.compile(null, emptyList(), emptyList(), unit);
    try {
      l.loadClass("123");
      fail();
    } catch (Exception e) {
      assertEquals(ClassNotFoundException.class, e.getClass());
      assertEquals("java.lang.ClassNotFoundException: 123", e.toString());
    }
  }

  @Test
  public void empty() {
    JavaUnit empty = new JavaUnit(new PackageDeclaration());
    assertEquals(Optional.empty(), empty.getEponymousDeclaration());
    assertEquals("", empty.list());
  }

  @Test
  public void enterprise() {
    JavaUnit unit = new JavaUnit("uss");
    ClassDeclaration enterprise = unit.declareClass("Enterprise").addModifier(Modifier.PUBLIC);
    enterprise.addInterface(ClassType.of(Supplier.class, String.class));
    enterprise.addFieldDeclaration(String.class, "text").addModifier("private", "final");
    enterprise.addFieldDeclaration(Number.class, "number").addModifier("private", "final");
    enterprise
        .declareConstructor()
        .addModifier(Modifier.PUBLIC)
        .addParameter(String.class, "text")
        .addParameter(Number.class, "number")
        .addStatement("this.text = text")
        .addStatement("this.number = number");
    enterprise
        .addMethodDeclaration(String.class, "get")
        .addAnnotation(Override.class)
        .addModifier(Modifier.PUBLIC)
        .addStatement("return text + '-' + number");

    Supplier<?> spaceship = unit.compile(Supplier.class, "NCC", (short) 1701);

    assertEquals("Enterprise", spaceship.getClass().getSimpleName());
    assertEquals("NCC-1701", spaceship.get());
    Text.assertEquals(getClass(), "enterprise", unit);
  }

  @Test
  public void processed() throws Exception {
    JavaUnit unit = new JavaUnit("test");
    ClassDeclaration enterprise = unit.declareClass("Class").addModifier(Modifier.PUBLIC);
    enterprise.addFieldDeclaration(Object.class, "field1").addAnnotation(Counter.Mark.class);
    enterprise.addFieldDeclaration(Object.class, "field2").addAnnotation(Counter.Mark.class);
    Text.assertEquals(getClass(), "processed", unit);

    Counter counter = new Counter();
    Compilation.compile(getClass().getClassLoader(), emptyList(), asList(counter), unit);
    assertEquals(2, counter.listOfElements.size());
  }

  @Test
  public void unnamed() throws Exception {
    JavaUnit unnamed = new JavaUnit(new PackageDeclaration());
    unnamed.declareClass("Unnamed").addModifier("public");
    assertEquals("Unnamed", unnamed.compile(Object.class).getClass().getTypeName());
    try {
      unnamed.compile(Object.class, "unused", "arguments");
      fail();
    } catch (Exception e) {
      assertEquals(true, e.toString().contains("compiling or instantiating failed"));
    }
    // with types supplier...
    Supplier<Class<?>[]> types = () -> new Class<?>[0];
    assertEquals("Unnamed", unnamed.compile(Object.class, types).getClass().getTypeName());
    try {
      types = () -> new Class<?>[1];
      unnamed.compile(Object.class, types);
      fail();
    } catch (Exception e) {
      assertEquals(true, e.toString().contains("compiling or instantiating failed"));
    }
  }
}
