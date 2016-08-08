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

import com.github.sormuras.listing.Name;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class TypeDeclaration extends ClassMemberDeclaration
    implements DeclarationContainer {

  private List<TypeDeclaration> declarations = Collections.emptyList();

  @Override
  public void assertValidNestedDeclarationName(String name) {
    DeclarationContainer.super.assertValidNestedDeclarationName(name);
    TypeDeclaration enclosing = this;
    while (enclosing != null) {
      TypeDeclaration parent = enclosing;
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

  @Override
  public ElementType getAnnotationTarget() {
    return ElementType.TYPE;
  }

  @Override
  public List<TypeDeclaration> getDeclarations() {
    if (declarations == Collections.EMPTY_LIST) {
      declarations = new ArrayList<>();
    }
    return declarations;
  }

  @Override
  public boolean isEmpty() {
    return isDeclarationsEmpty();
  }

  public boolean isDeclarationsEmpty() {
    return declarations.isEmpty();
  }

  /** Return simple name representation of this type declaration. */
  public Name toName() {
    String packageName = "";
    if (getCompilationUnit() != null) {
      packageName = getCompilationUnit().getPackageName();
    }
    List<String> simpleNames = new ArrayList<>();
    TypeDeclaration current = this;
    while (current != null) {
      simpleNames.add(0, current.getName());
      current = current.getEnclosingDeclaration();
    }
    Name name = new Name(packageName, simpleNames);
    name.getModifiers().addAll(getModifiers());
    name.setTarget(ElementType.TYPE);
    return name;
  }
}
