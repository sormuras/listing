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
package com.github.sormuras.listing.unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class TypeDeclaration implements DeclarationContainer {

  private CompilationUnit compilationUnit;
  private List<TypeDeclaration> declarations = Collections.emptyList();
  private TypeDeclaration enclosingDeclaration;
  private String name;

  @Override
  public void assertValidNestedDeclarationName(String name) {
    DeclarationContainer.super.assertValidNestedDeclarationName(name);
    Optional<TypeDeclaration> enclosing = Optional.of(this);
    while (enclosing.isPresent()) {
      TypeDeclaration parent = enclosing.get();
      if (name.equals(parent.getName())) {
        throw new IllegalArgumentException("nested " + name + " hides an enclosing type");
      }
      enclosing = parent.getEnclosingDeclaration();
    }
  }

  @Override
  public <T extends TypeDeclaration> T declare(T declaration, String name) {
    DeclarationContainer.super.declare(declaration, name);
    declaration.setEnclosingDeclaration(this);
    declaration.setCompilationUnit(getCompilationUnit());
    return declaration;
  }

  public CompilationUnit getCompilationUnit() {
    return compilationUnit;
  }

  @Override
  public List<TypeDeclaration> getDeclarations() {
    if (declarations == Collections.EMPTY_LIST) {
      declarations = new ArrayList<>();
    }
    return declarations;
  }

  public Optional<TypeDeclaration> getEnclosingDeclaration() {
    return Optional.ofNullable(enclosingDeclaration);
  }

  public String getName() {
    return name;
  }

  public boolean isDeclarationsEmpty() {
    return declarations.isEmpty();
  }

  public void setCompilationUnit(CompilationUnit compilationUnit) {
    this.compilationUnit = compilationUnit;
  }

  public void setEnclosingDeclaration(TypeDeclaration enclosingDeclaration) {
    this.enclosingDeclaration = enclosingDeclaration;
  }

  public void setName(String name) {
    this.name = name;
  }
}
