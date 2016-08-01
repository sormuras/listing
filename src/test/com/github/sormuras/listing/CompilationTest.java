package com.github.sormuras.listing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import javax.tools.JavaFileObject;
import org.junit.jupiter.api.Test;

class CompilationTest {

  @Test
  void hi() throws Exception {
    String code = "public class Hi { public String greet(String who) { return \"Hi \" + who;}}";
    JavaFileObject file = Compilation.source("Hi.java", code);
    ClassLoader loader = Compilation.compile(file);
    Class<?> hiClass = loader.loadClass("Hi");
    Object hiInstance = hiClass.newInstance();
    Method greetMethod = hiClass.getMethod("greet", String.class);
    assertEquals("Hi world", greetMethod.invoke(hiInstance, "world"));
    assertThrows(ClassNotFoundException.class, () -> loader.loadClass("Hello"));
  }

  @Test
  void lastModified() {
    JavaFileObject jfo = new Compilation.CharContentFileObject("abc", "abc");
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
    JavaFileObject fileA = Compilation.source("listing/A.java", codeA);
    JavaFileObject fileB = Compilation.source("listing/B.java", codeB);
    Compilation.compile(fileA, fileB);
  }

  @Test
  void syntaxError() {
    assertThrows(
        Exception.class, () -> Compilation.compile(Compilation.source("F.java", "class 1F {}")));
  }
}
