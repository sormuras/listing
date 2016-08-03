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

import com.github.sormuras.listing.Listing;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class TypeDeclaration extends ClassMemberDeclaration
    implements DeclarationContainer {

  private List<TypeDeclaration> declarations = Collections.emptyList();
  private final String keyword;

  public TypeDeclaration(String keyword) {
    this.keyword = keyword;
  }

  @Override
  public Listing apply(Listing listing) {
    listing.newline();
    applyDeclarationHead(listing);
    applyDeclarationBody(listing);
    return listing;
  }

  protected Listing applyDeclarationBody(Listing listing) {
    listing.add(' ').add('{').newline();
    listing.indent(1);
    if (!isDeclarationsEmpty()) {
      getDeclarations().forEach(listing::add);
    }
    listing.indent(-1).add('}').newline();
    return listing;
  }

  protected Listing applyDeclarationHead(Listing listing) {
    listing.add(toAnnotationsListable());
    listing.add(toModifiersListable());
    listing.add(getKeyword()).add(' ').add(getName());
    return listing;
  }

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
    declaration.setCompilationUnit(getCompilationUnit().orElse(null));
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

  public String getKeyword() {
    return keyword;
  }

  public boolean isDeclarationsEmpty() {
    return declarations.isEmpty();
  }

  @Override
  public TypeDeclaration setName(String name) {
    super.setName(name);
    return this;
  }
}
