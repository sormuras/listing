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

import com.github.sormuras.listing.Listable;
import java.util.List;
import javax.lang.model.SourceVersion;

public interface DeclarationContainer extends Listable {

  default void assertValidNestedDeclarationName(String name) {
    if (!SourceVersion.isName(name)) {
      throw new IllegalArgumentException("expected valid name, but got: \"" + name + "\"");
    }
    if (getDeclarations().stream().filter(d -> d.getName().equals(name)).findAny().isPresent()) {
      throw new IllegalArgumentException("duplicate nested type " + name);
    }
  }

  default <T extends TypeDeclaration> T declare(T declaration, String name) {
    assertValidNestedDeclarationName(name);
    declaration.setName(name);
    getDeclarations().add(declaration);
    return declaration;
  }

  default NormalClassDeclaration declareClass(String name) {
    return declare(new NormalClassDeclaration(), name);
  }

  List<TypeDeclaration> getDeclarations();
}
