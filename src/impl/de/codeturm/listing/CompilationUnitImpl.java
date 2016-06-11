package de.codeturm.listing;

import java.util.ArrayList;
import java.util.List;

public class CompilationUnitImpl implements CompilationUnit {

  private List<TypeDeclaration> declarations = new ArrayList<>();

  @Override
  public List<TypeDeclaration> getDeclarations() {
    return declarations;
  }
}
