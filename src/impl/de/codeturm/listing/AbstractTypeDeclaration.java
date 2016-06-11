package de.codeturm.listing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractTypeDeclaration implements TypeDeclaration {

  private CompilationUnit compilationUnit;
  private List<TypeDeclaration> declarations;
  private TypeDeclaration enclosingDeclaration;
  private String name;

  @Override
  public CompilationUnit getCompilationUnit() {
    return compilationUnit;
  }

  @Override
  public List<TypeDeclaration> getDeclarations() {
    if (declarations == null) {
      declarations = new ArrayList<>();
    }
    return declarations;
  }

  @Override
  public Optional<TypeDeclaration> getEnclosingDeclaration() {
    return Optional.ofNullable(enclosingDeclaration);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setCompilationUnit(CompilationUnit compilationUnit) {
    this.compilationUnit = compilationUnit;
  }

  @Override
  public void setEnclosingDeclaration(TypeDeclaration enclosingDeclaration) {
    this.enclosingDeclaration = enclosingDeclaration;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }
}
