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
package com.github.sormuras.listing;

import java.util.List;

/**
 * Collects {@linkplain TypeDeclaration}s.
 */
public interface Container extends Listable {

  <D extends TypeDeclaration<D>> D addDeclaredType(D declaration);

  default AnnotationDeclaration declareAnnotation(String name) {
    AnnotationDeclaration declaration = new AnnotationDeclaration();
    declaration.setName(name);
    return addDeclaredType(declaration);
  }

  default ClassDeclaration declareClass(String name) {
    ClassDeclaration declaration = new ClassDeclaration();
    declaration.setName(name);
    return addDeclaredType(declaration);
  }

  default EnumDeclaration declareEnum(String name) {
    EnumDeclaration declaration = new EnumDeclaration();
    declaration.setName(name);
    return addDeclaredType(declaration);
  }

  default InterfaceDeclaration declareInterface(String name) {
    InterfaceDeclaration declaration = new InterfaceDeclaration();
    declaration.setName(name);
    return addDeclaredType(declaration);
  }

  List<TypeDeclaration<?>> getDeclaredTypes();
}
