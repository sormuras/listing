package com.github.sormuras.listing;

import static org.junit.Assert.assertNotEquals;

import java.net.URI;

import javax.tools.JavaFileObject;

import org.junit.Test;

public class CompilationTest {

  @Test
  public void charContentFileObject() throws Exception {
    JavaFileObject jfo = new Compilation.CharContentFileObject(URI.create("abc"), "abc");
    assertNotEquals(0L, jfo.getLastModified());
  }
}
