package com.github.sormuras.listing.unit;

public interface Units {

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

    ClassDeclaration xxx = rayInit.declareLocalEnum("XXX");
    rayInit.add(xxx.getName() + ".class.getName();");
    return unit;
  }
}
