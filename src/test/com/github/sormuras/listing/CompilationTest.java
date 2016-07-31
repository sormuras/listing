package com.github.sormuras.listing;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Method;
import java.net.URI;

import javax.tools.JavaFileObject;

import org.junit.jupiter.api.Test;

class CompilationTest {

  @Test
  void hello() throws Exception {
    String code =
        "public class Hello { public String greet(String name) { return \"Hello \" + name;}}";
    JavaFileObject file = Compilation.source(URI.create("Hello.java"), code);
    ClassLoader loader = Compilation.compile(file);
    Class<?> helloClass = loader.loadClass("Hello");
    Object helloInstance = helloClass.newInstance();
    Method greetMethod = helloClass.getMethod("greet", String.class);
    assertEquals("Hello world", greetMethod.invoke(helloInstance, "world"));
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
    Compilation.compile(getClass().getClassLoader(), emptyList(), emptyList(), asList(a, b));
  }
}
