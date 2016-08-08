package com.github.sormuras.listing.unit;

import com.github.sormuras.listing.Annotatable.AbstractAnnotatable;
import com.github.sormuras.listing.Listable;

/** Named and encloseable member base class. */
public abstract class AbstractMember extends AbstractAnnotatable implements Listable {

  private CompilationUnit compilationUnit = null;
  private TypeDeclaration enclosingDeclaration = null;
  private String name;

  public AbstractMember() {
    super();
  }

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
