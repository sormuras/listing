package de.codeturm.listing;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface Fixtures {

  static CompilationUnit simple() {
    CompilationUnit unit = new CompilationUnit();
    unit.declareClass("Alpha");
    unit.declareClass("Beta");
    unit.declareClass("Gamma").declareClass("Ray");
    return unit;
  }

  static String text(Class<?> testClass, String testName) {
    String fileName = testClass.getName().replace('.', '/') + "." + testName + ".txt";
    try {
      Path path = Paths.get(testClass.getClassLoader().getResource(fileName).toURI());
      return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new AssertionError("Loading text from file `" + fileName + "` failed!", e);
    }
  }
}
