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

public class NormalClassDeclaration extends ClassDeclaration {

  private ClassType superClass = null;
  private List<TypeParameter> typeParameters = Collections.emptyList();

  public NormalClassDeclaration() {
    this("Unnamed");
  }

  public NormalClassDeclaration(String name) {
    setName(name);
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
    listing.add(' ').add('{').newline();
    listing.indent(1);
    if (!isDeclarationsEmpty()) {
      getDeclarations().forEach(listing::add);
    }
    listing.add(getClassBodyElements());
    if (!isInitializersEmpty()) {
      getInitializers().forEach(listing::add);
    }
    listing.indent(-1).add('}').newline();
    return listing;
  }

  public ClassDeclaration addTypeParameter(TypeParameter typeParameter) {
    getTypeParameters().add(typeParameter);
    return this;
  }

  public MethodDeclaration declareConstructor() {
    return declareMethod(void.class, "<init>");
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
