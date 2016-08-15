package com.github.sormuras.listing.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.expectThrows;

import com.github.sormuras.listing.Name;
import com.github.sormuras.listing.Tests;
import com.github.sormuras.listing.type.ClassType;
import com.github.sormuras.listing.type.TypeArgument;
import com.github.sormuras.listing.type.TypeVariable;
import java.lang.annotation.ElementType;
import java.util.List;
import org.junit.jupiter.api.Test;

class MethodDeclarationTest {

  @Test
  void constructor() {
    MethodDeclaration m = new MethodDeclaration();
    m.setName("<init>");
    assertEquals("<init>();\n", m.list());
    assertEquals(ElementType.METHOD, m.getAnnotationTarget());
    assertEquals(true, m.isConstructor());
    assertEquals(false, m.isModified());
    assertEquals(false, m.isVarArgs());
    Exception expected = expectThrows(IllegalStateException.class, () -> m.setVarArgs(true));
    assertEquals(true, expected.toString().contains("no parameter"));
    // put into context
    m.setEnclosingDeclaration(NormalClassDeclaration.of("Abc"));
    assertEquals("Abc();\n", m.list());
  }

  @Test
  void declaration() {
    MethodDeclaration m = new MethodDeclaration();
    m.setName("m");
    assertEquals("void m();\n", m.list());
    assertEquals(ElementType.METHOD, m.getAnnotationTarget());
    assertEquals(false, m.isConstructor());
    assertEquals(false, m.isModified());
    assertEquals(false, m.isVarArgs());
    Exception expected = expectThrows(IllegalStateException.class, () -> m.setVarArgs(true));
    assertEquals(true, expected.toString().contains("no parameter"));
  }

  @Test
  void emptyList() {
    MethodDeclaration emptyList = new MethodDeclaration();
    emptyList.addAnnotation(SuppressWarnings.class, "unchecked");
    emptyList.addModifier("public", "static", "final");
    emptyList.addTypeParameter(new TypeParameter());
    emptyList.setReturnType(ClassType.of(Name.of(List.class), TypeArgument.of(new TypeVariable())));
    emptyList.setName("emptyList");
    emptyList.addStatement("return (List<T>) EMPTY_LIST");
    Tests.assertEquals(getClass(), "emptyList", emptyList);
  }

  @Test
  void runnable() {
    MethodDeclaration runnable = new MethodDeclaration();
    runnable.addAnnotation(Override.class);
    runnable.addModifier("public");
    runnable.setName("run");
    runnable.addParameter(getClass(), "this");
    runnable.addThrows(RuntimeException.class);
    runnable.addThrows(TypeVariable.of("X"));
    runnable.addStatement("java.lang.System.out.println({S})", "Running!");
    Tests.assertEquals(getClass(), "runnable", runnable);
    assertEquals(true, runnable.isModified());
    assertEquals(false, runnable.isVarArgs());
    assertSame(runnable, runnable.getParameters().get(0).getMethodDeclaration().get());
    Exception expected = expectThrows(IllegalStateException.class, () -> runnable.setVarArgs(true));
    assertEquals(true, expected.toString().contains("array type expected"));
  }

  @Test
  void varArgs() {
    MethodDeclaration var = new MethodDeclaration();
    var.setName("var");
    var.addParameter(int[].class, "numbers");
    assertEquals(false, var.isVarArgs());
    assertEquals("void var(int[] numbers);\n", var.list());
    var.setVarArgs(true);
    assertEquals(true, var.isVarArgs());
    assertEquals("void var(int... numbers);\n", var.list());
    var.setVarArgs(false);
    assertEquals(false, var.isVarArgs());
    assertEquals("void var(int[] numbers);\n", var.list());
  }
}
