/*
 * Copyright (C) 2016 Christian Stein
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
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
