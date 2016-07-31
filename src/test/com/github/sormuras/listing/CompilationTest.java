package com.github.sormuras.listing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import java.net.URI;

import javax.tools.JavaFileObject;

import org.junit.jupiter.api.Test;

class CompilationTest {

  @Test
  void hi() throws Exception {
    String code = "public class Hi { public String greet(String who) { return \"Hi \" + who;}}";
    JavaFileObject file = Compilation.source(URI.create("Hi.java"), code);
    ClassLoader loader = Compilation.compile(file);
    Class<?> hiClass = loader.loadClass("Hi");
    Object hiInstance = hiClass.newInstance();
    Method greetMethod = hiClass.getMethod("greet", String.class);
    assertEquals("Hi world", greetMethod.invoke(hiInstance, "world"));
    assertThrows(ClassNotFoundException.class, () -> loader.loadClass("Hello"));
  }

  @Test
  void lastModified() {
    JavaFileObject jfo = new Compilation.CharContentFileObject(URI.create("abc"), "abc");
    assertNotEquals(0L, jfo.getLastModified());
  }

  @Test
  void multipleClassesWithDependingOnEachOther() {
    // JavaUnit a = new JavaUnit("a");
    // a.declareClass("A").addModifier("public");
    // JavaUnit b = new JavaUnit("b");
    // b.declareClass("B").setSuperClass(ClassType.of("a", "A"));
    String codeA = "package a; public class A {}";
    String codeB = "package b; class B extends a.A {}";
    JavaFileObject a = Compilation.source(URI.create("listing/A.java"), codeA);
    JavaFileObject b = Compilation.source(URI.create("listing/B.java"), codeB);
    Compilation.compile(a, b);
  }

  @Test
  void syntaxError() {
    assertThrows(
        RuntimeException.class,
        () -> Compilation.compile(Compilation.source(URI.create("F.java"), "class 1F {}")));
  }
}
