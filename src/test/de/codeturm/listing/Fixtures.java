package de.codeturm.listing;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface Fixtures {

  static CompilationUnit simple() {
    CompilationUnit unit = new CompilationUnit();

    ClassDeclaration alpha = unit.declareClass("Alpha");
    alpha.declareClass("Removed");
    alpha.declareInitializer(true);
    alpha.getDeclarations().clear();
    alpha.getInitializers().clear();

    ClassDeclaration beta = unit.declareClass("Beta");
    beta.declareInitializer(true).add(l -> l.add("// init of ").add(beta.getName()).newline());

    ClassDeclaration gamma = unit.declareClass("Gamma");
    ClassDeclaration ray = gamma.declareClass("Ray");
    Initializer rayInit = ray.declareInitializer(false);

    ClassDeclaration xxx = rayInit.declareLocalClass("XXX");
    rayInit.add(xxx.getName() + ".class.getName();");
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
