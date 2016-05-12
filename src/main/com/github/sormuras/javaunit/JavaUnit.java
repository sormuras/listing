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
package com.github.sormuras.javaunit;

import java.util.ArrayList;
import java.util.List;

/**
 * JavaFile models compilation unit.
 *
 * <pre>
 * CompilationUnit:
 *   [PackageDeclaration] {ImportDeclaration} {TypeDeclaration}
 * </pre>
 *
 * @author Christian Stein
 * @see http://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.3
 */
public class JavaUnit implements Container {

  private final PackageDeclaration packageDeclaration;
  private final ImportDeclarations importDeclarations;
  private final List<TypeDeclaration<?>> types;

  public JavaUnit(String packageName) {
    this.packageDeclaration = new PackageDeclaration(packageName);
    this.importDeclarations = new ImportDeclarations();
    this.types = new ArrayList<>();
  }

  @Override
  public Listing apply(Listing listing) {
    listing.add(packageDeclaration);
    if (!listing.isLastLineEmpty() && !importDeclarations.isEmpty()) {
      listing.newline();
    }
    listing.add(importDeclarations);
    if (!listing.isLastLineEmpty() && !getDeclaredTypes().isEmpty()) {
      listing.newline();
    }
    getDeclaredTypes().forEach(listing::add);
    return listing;
  }

  public ImportDeclarations getImportDeclarations() {
    return importDeclarations;
  }

  public PackageDeclaration getPackageDeclaration() {
    return packageDeclaration;
  }

  @Override
  public <D extends TypeDeclaration<D>> D addDeclaredType(D declaration) {
    declaration.setUnit(this);
    declaration.setEnclosingType(null);
    getDeclaredTypes().add(declaration);
    return declaration;
  }

  @Override
  public List<TypeDeclaration<?>> getDeclaredTypes() {
    return types;
  }
}
