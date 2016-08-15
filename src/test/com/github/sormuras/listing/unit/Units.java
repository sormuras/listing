package com.github.sormuras.listing.unit;

import com.github.sormuras.listing.type.ClassName;
import com.github.sormuras.listing.type.ClassType;
import com.github.sormuras.listing.type.Counter;
import com.github.sormuras.listing.type.TypeArgument;
import javax.lang.model.element.Modifier;

public interface Units {

  static CompilationUnit abc() {
    ClassType i = ClassType.of("", "I");

    ClassName nameA = ClassName.of("A");
    nameA.getTypeArguments().add(TypeArgument.of(i));
    ClassName nameB = ClassName.of("B");
    nameB.getTypeArguments().add(TypeArgument.of(i));
    nameB.getTypeArguments().add(TypeArgument.of(i));
    ClassName nameC = ClassName.of("C");
    nameC.getTypeArguments().add(TypeArgument.of(i));
    nameC.getTypeArguments().add(TypeArgument.of(i));
    nameC.getTypeArguments().add(TypeArgument.of(i));

    CompilationUnit unit = new CompilationUnit();
    unit.declareInterface("I");
    NormalClassDeclaration a = unit.declareClass("A");
    a.addModifier(Modifier.PUBLIC);
    a.addTypeParameter(TypeParameter.of("U"));
    a.declareField(ClassType.of("", "A", "B", "C"), "raw").addAnnotation(Counter.Mark.class);
    a.declareField(ClassType.of("", nameA, nameB, nameC), "parametered")
        .addAnnotation(Counter.Mark.class);
    NormalClassDeclaration b = a.declareClass("B");
    b.addTypeParameter(TypeParameter.of("V"));
    b.addTypeParameter(TypeParameter.of("W"));
    NormalClassDeclaration c = b.declareClass("C");
    c.addTypeParameter(TypeParameter.of("X"));
    c.addTypeParameter(TypeParameter.of("Y"));
    c.addTypeParameter(TypeParameter.of("Z"));
    return unit;
  }

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
    gamma.addModifier(Modifier.PUBLIC);
    ClassDeclaration ray = gamma.declareClass("Ray");
    Initializer rayInit = ray.declareInitializer(false);

    ClassDeclaration xxx = rayInit.declareLocalEnum("XXX");
    rayInit.add(xxx.getName() + ".class.getName();");
    rayInit.declareLocalClass("ZZZ");

    return unit;
  }
}
