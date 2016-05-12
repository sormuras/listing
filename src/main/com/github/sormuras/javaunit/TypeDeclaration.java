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

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;

/**
 * Class, interface, enum or annotation declaration.
 *
 * @param <T> actual implementation type.
 */
public abstract class TypeDeclaration<T> extends ClassMemberDeclaration<T> implements Container {

  private Listable body = null;
  private final List<TypeDeclaration<?>> declaredTypes = new ArrayList<>();
  private final String keyword;

  protected TypeDeclaration(String keyword) {
    this.keyword = keyword;
  }

  @Override
  public <D extends TypeDeclaration<D>> D addDeclaredType(D declaration) {
    declaration.setUnit(getUnit().orElse(null));
    declaration.setEnclosingType(this);
    getDeclaredTypes().add(declaration);
    return declaration;
  }

  @Override
  public Listing apply(Listing listing) {
    listing.newline();
    applyDeclarationHead(listing);
    applyDeclarationBody(listing);
    return listing;
  }

  protected Listing applyDeclarationBody(Listing listing) {
    listing.add(" {").newline().indent(1);
    if (body != null) {
      listing.add(body);
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
  public ElementType getAnnotationTarget() {
    return ElementType.TYPE;
  }

  public Listable getBody() {
    return body;
  }

  @Override
  public List<TypeDeclaration<?>> getDeclaredTypes() {
    return declaredTypes;
  }

  public String getKeyword() {
    return keyword;
  }

  public void setBody(Listable body) {
    this.body = body;
  }
}
