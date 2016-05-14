package com.github.sormuras.listing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.lang.annotation.ElementType;
import java.util.List;

import org.junit.Test;

import com.github.sormuras.listing.ClassType;
import com.github.sormuras.listing.JavaName;
import com.github.sormuras.listing.MethodDeclaration;
import com.github.sormuras.listing.TypeArgument;
import com.github.sormuras.listing.TypeParameter;
import com.github.sormuras.listing.TypeVariable;

public class MethodDeclarationTest {

  @Test
  public void declaration() {
    MethodDeclaration m = new MethodDeclaration().setName("m");
    assertEquals("void m()\n", m.list());
    assertEquals(ElementType.METHOD, m.getAnnotationTarget());
    assertEquals(false, m.isModified());
    assertEquals(false, m.isVarArgs());
    try {
      m.setVarArgs(true);
      fail();
    } catch (IllegalStateException expected) {
      assertEquals(true, expected.toString().contains("no parameter"));
    }
  }

  @Test
  public void emptyList() {
    MethodDeclaration emptyList = new MethodDeclaration();
    emptyList.addAnnotation(SuppressWarnings.class, "unchecked");
    emptyList.addModifier("public", "static", "final");
    emptyList.addTypeParameter(new TypeParameter("T"));
    emptyList.setReturnType(
        new ClassType(JavaName.of(List.class), new TypeArgument(new TypeVariable().setName("T"))));
    emptyList.setName("emptyList");
    emptyList.setBody(code -> code.add("return (List<T>) EMPTY_LIST;").newline());
    Text.assertEquals(getClass(), "emptyList", emptyList);
  }

  @Test
  public void runnable() {
    MethodDeclaration runnable = new MethodDeclaration();
    runnable.addAnnotation(Override.class);
    runnable.addModifier("public");
    runnable.setName("run");
    runnable.addParameter(getClass(), "this");
    runnable.addThrows(RuntimeException.class);
    runnable.addThrows(new TypeVariable().setName("X"));
    runnable.setBody(code -> code.add("java.lang.System.out.println(\"Running!\");").newline());
    Text.assertEquals(getClass(), "runnable", runnable);
    assertEquals(true, runnable.isModified());
    assertEquals(false, runnable.isVarArgs());
    assertSame(runnable, runnable.getParameters().get(0).getMethodDeclaration().get());
    try {
      runnable.setVarArgs(true);
      fail();
    } catch (IllegalStateException expected) {
      assertEquals(true, expected.toString().contains("array type expected"));
    }
  }

  @Test
  public void varArgs() {
    MethodDeclaration var = new MethodDeclaration();
    var.setName("var");
    var.addParameter(int[].class, "numbers");
    assertEquals(false, var.isVarArgs());
    assertEquals("void var(int[] numbers)\n", var.list());
    var.setVarArgs(true);
    assertEquals(true, var.isVarArgs());
    assertEquals("void var(int... numbers)\n", var.list());
    var.setVarArgs(false);
    assertEquals(false, var.isVarArgs());
    assertEquals("void var(int[] numbers)\n", var.list());
  }
}
