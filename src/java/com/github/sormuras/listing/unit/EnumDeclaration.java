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
import java.util.Collections;
import java.util.List;

public class EnumDeclaration extends ClassDeclaration {

  @Override
  public ClassDeclaration addTypeParameter(TypeParameter typeParameter) {
    throw new UnsupportedOperationException("Enum don't support type parameters!");
  }

  @Override
  public Listing apply(Listing listing) {
    if (!isLocal()) {
      listing.newline();
    }
    listing.add(toAnnotationsListable());
    listing.add(toModifiersListable());
    listing.add("enum").add(' ').add(getName());
    // [Superinterfaces]
    if (!isInterfacesEmpty()) {
      listing.add(" implements ").add(getInterfaces(), ", ");
    }
    listing.add(' ').add('{').newline();
    listing.indent(1);
    // TODO add enum constants - simple or fancy local classes
    // TODO add ";"
    if (!isDeclarationsEmpty()) {
      getDeclarations().forEach(listing::add);
    }
    // TODO add other class members like fields, methods...
    listing.indent(-1).add('}').newline();
    return listing;
  }

  @Override
  public ClassType getSuperClass() {
    return ClassType.of(Enum.class);
  }

  @Override
  public List<TypeParameter> getTypeParameters() {
    return Collections.emptyList();
  }

  @Override
  public ClassDeclaration setSuperClass(ClassType superClass) {
    throw new UnsupportedOperationException("Super class of enum can't be set!");
  }
}
