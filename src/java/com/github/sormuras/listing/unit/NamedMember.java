package com.github.sormuras.listing.unit;

import com.github.sormuras.listing.Annotated;

/** Named, annotatable and encloseable member base class. */
public abstract class NamedMember extends Annotated {

  private CompilationUnit compilationUnit = null;
  private TypeDeclaration enclosingDeclaration = null;
  private String name;

  public CompilationUnit getCompilationUnit() {
    return compilationUnit;
  }

  public TypeDeclaration getEnclosingDeclaration() {
    return enclosingDeclaration;
  }

  public String getName() {
    return name;
  }

  public void setCompilationUnit(CompilationUnit unit) {
    this.compilationUnit = unit;
  }

  public void setEnclosingDeclaration(TypeDeclaration enclosingType) {
    this.enclosingDeclaration = enclosingType;
  }

  public void setName(String name) {
    this.name = name;
  }
}
