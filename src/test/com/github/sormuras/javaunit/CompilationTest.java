package com.github.sormuras.javaunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.net.URI;
import java.util.Scanner;

import javax.tools.JavaFileObject;

import org.junit.Test;

public class CompilationTest {

  @Test
  @SuppressWarnings("resource")
  public void charContentFileObject() throws Exception {
    JavaFileObject jfo = new Compilation.CharContentFileObject(URI.create("abc"), "abc");
    assertNotEquals(0L, jfo.getLastModified());
    assertEquals("abc", new Scanner(jfo.openInputStream()).useDelimiter("\\A").next());
  }
}
