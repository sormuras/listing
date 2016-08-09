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
import com.github.sormuras.listing.type.ClassType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Normal class declaration.
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.1
 */
public class NormalClassDeclaration extends ClassDeclaration {

  public static NormalClassDeclaration of(String name) {
    NormalClassDeclaration declaration = new NormalClassDeclaration();
    declaration.setName(name);
    return declaration;
  }

  private ClassType superClass = null;
  private List<TypeParameter> typeParameters = Collections.emptyList();

  public ClassDeclaration addTypeParameter(TypeParameter typeParameter) {
    getTypeParameters().add(typeParameter);
    return this;
  }

  @Override
  public Listing apply(Listing listing) {
    if (!isLocal()) {
      listing.newline();
    }
    listing.add(toAnnotationsListable());
    listing.add(toModifiersListable());
    listing.add("class").add(' ').add(getName());
    // [TypeParameters]
    if (!isTypeParametersEmpty()) {
      listing.add('<').add(getTypeParameters(), ", ").add('>');
    }
    // [Superclass]
    if (getSuperClass() != null) {
      listing.add(" extends ").add(getSuperClass());
    }
    // [Superinterfaces]
    if (!isInterfacesEmpty()) {
      listing.add(" implements ").add(getInterfaces(), ", ");
    }
    applyClassBody(listing);
    return listing;
  }

  public ClassType getSuperClass() {
    return superClass;
  }

  public List<TypeParameter> getTypeParameters() {
    if (typeParameters == Collections.EMPTY_LIST) {
      typeParameters = new ArrayList<>();
    }
    return typeParameters;
  }

  public boolean isTypeParametersEmpty() {
    return typeParameters.isEmpty();
  }

  public ClassDeclaration setSuperClass(ClassType superClass) {
    this.superClass = superClass;
    return this;
  }
}
