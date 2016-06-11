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

import java.util.Optional;

public interface TypeDeclaration extends DeclarationContainer {

  @Override
  default void assertValidNestedDeclarationName(String name) {
    DeclarationContainer.super.assertValidNestedDeclarationName(name);
    TypeDeclaration enclosing = this;
    if (enclosing.getEnclosingDeclaration().isPresent()) {
      while (enclosing.getEnclosingDeclaration().isPresent()) {
        if (enclosing.getName().equals(name)) {
          throw new IllegalArgumentException("nested " + name + " hides an enclosing type");
        }
        enclosing = enclosing.getEnclosingDeclaration().get();
      }
    }
  }

  @Override
  default <T extends TypeDeclaration> T declare(T declaration, String name) {
    DeclarationContainer.super.declare(declaration, name);
    declaration.setEnclosingDeclaration(this);
    declaration.setCompilationUnit(getCompilationUnit());
    return declaration;
  }

  CompilationUnit getCompilationUnit();

  Optional<TypeDeclaration> getEnclosingDeclaration();

  String getName();

  void setCompilationUnit(CompilationUnit unit);

  void setEnclosingDeclaration(TypeDeclaration declaration);

  void setName(String name);
}
