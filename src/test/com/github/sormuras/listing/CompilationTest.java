package com.github.sormuras.listing;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertNotEquals;

import java.net.URI;
import javax.tools.JavaFileObject;

import org.junit.Test;

public class CompilationTest {

  @Test
  public void lastModified() throws Exception {
    JavaFileObject jfo = new Compilation.CharContentFileObject(URI.create("abc"), "abc");
    assertNotEquals(0L, jfo.getLastModified());
  }

  @Test
  public void multipleUnitsWithDependingOnEachOther() throws Exception {
    JavaUnit a = new JavaUnit("a");
    a.declareClass("A").addModifier("public");
    JavaUnit b = new JavaUnit("b");
    b.declareClass("B").setSuperClass(ClassType.of("a", "A"));
    Compilation.compile(getClass().getClassLoader(), emptyList(), emptyList(), a, b);
  }
}
