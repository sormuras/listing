package de.codeturm.listing;

public class Listing {

  public String toString(CompilationUnit unit) {
    Lines lines = new Lines();
    toString(lines, unit);
    return lines.toString();
  }

  public void toString(Lines lines, CompilationUnit unit) {
    unit.getDeclarations().forEach(toplevel -> toString(lines, toplevel));
  }

  public void toString(Lines lines, TypeDeclaration declaration) {
    lines.newline();
    lines.add(declaration.getKeyword()).add(' ').add(declaration.getName());
    lines.add(' ').add('{').newline();
    lines.indent(1);
    declaration.getDeclarations().forEach(nested -> toString(lines, nested));
    lines.indent(-1).add('}').newline();
  }
}
