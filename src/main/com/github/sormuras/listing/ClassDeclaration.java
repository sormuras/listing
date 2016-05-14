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

import java.util.ArrayList;
import java.util.List;

/**
 * A class declaration specifies a new named reference type.
 *
 * @author Christian Stein
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.1
 */
public class ClassDeclaration extends TypeDeclaration<ClassDeclaration> {

  private final List<ClassType> interfaces;
  private ClassType superClass;
  private List<TypeParameter> typeParameters;
  private List<Listable> classBodyElements;

  public ClassDeclaration() {
    super("class");
    this.interfaces = new ArrayList<>();
    this.typeParameters = new ArrayList<>();
    this.classBodyElements = new ArrayList<>();
  }

  public ClassDeclaration addInterface(JavaType<?> interfaceType) {
    interfaces.add((ClassType) interfaceType);
    return this;
  }

  public ClassDeclaration addTypeParameter(TypeParameter typeParameter) {
    typeParameters.add(typeParameter);
    return this;
  }

  @Override
  protected Listing applyDeclarationBody(Listing listing) {
    if (getBody() != null) {
      return super.applyDeclarationBody(listing);
    }
    listing.add(" {").newline().indent(1);
    getDeclaredTypes().forEach(listing::add);
    listing.add(classBodyElements);
    listing.indent(-1).add('}').newline();
    return listing;
  }

  @Override
  protected Listing applyDeclarationHead(Listing listing) {
    super.applyDeclarationHead(listing);
    // [TypeParameters]
    if (!typeParameters.isEmpty()) {
      listing.add('<').add(typeParameters, ", ").add('>');
    }
    // [Superclass]
    if (superClass != null) {
      listing.add(" extends ").add(superClass);
    }
    // [Superinterfaces]
    if (!interfaces.isEmpty()) {
      listing.add(" implements ").add(interfaces, ", ");
    }
    return listing;
  }

  public MethodDeclaration declareConstructor() {
    MethodDeclaration declaration = new MethodDeclaration();
    declaration.setUnit(getUnit().orElse(null));
    declaration.setEnclosingType(this);
    declaration.setName("<init>");
    classBodyElements.add(declaration);
    return declaration;
  }

  public FieldDeclaration declareField(Class<?> type, String name) {
    return declareField(JavaType.of(type), name);
  }

  public FieldDeclaration declareField(JavaType<?> type, String name) {
    FieldDeclaration declaration = new FieldDeclaration();
    declaration.setUnit(getUnit().orElse(null));
    declaration.setEnclosingType(this);
    declaration.setType(type);
    declaration.setName(name);
    classBodyElements.add(declaration);
    return declaration;
  }

  public MethodDeclaration declareMethod(Class<?> type, String name) {
    return declareMethod(JavaType.of(type), name);
  }

  public MethodDeclaration declareMethod(JavaType<?> type, String name) {
    MethodDeclaration declaration = new MethodDeclaration();
    declaration.setUnit(getUnit().orElse(null));
    declaration.setEnclosingType(this);
    declaration.setReturnType(type);
    declaration.setName(name);
    classBodyElements.add(declaration);
    return declaration;
  }

  public ClassDeclaration setSuperClass(JavaType<?> superClass) {
    this.superClass = (ClassType) superClass;
    return this;
  }
}
