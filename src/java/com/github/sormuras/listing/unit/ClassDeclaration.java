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
import com.github.sormuras.listing.Listing;
import com.github.sormuras.listing.type.ClassType;
import com.github.sormuras.listing.type.JavaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class declaration specifies a new named reference type.
 *
 * <p>There are two kinds of class declarations: normal class declarations and enum declarations.
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.1
 */
public abstract class ClassDeclaration extends TypeDeclaration {

  private List<Listable> classBodyElements = new ArrayList<>();
  private List<Initializer> initializers = Collections.emptyList();
  private List<ClassType> interfaces = Collections.emptyList();
  private boolean local = false;

  public ClassDeclaration addInterface(JavaType interfaceType) {
    getInterfaces().add((ClassType) interfaceType);
    return this;
  }

  /** Applies class body. */
  public Listing applyClassBody(Listing listing) {
    listing.add(' ').add('{').newline();
    listing.indent(1);
    applyClassBodyElements(listing);
    listing.indent(-1);
    listing.add('}').newline();
    return listing;
  }

  /** Applies class body. */
  public Listing applyClassBodyElements(Listing listing) {
    if (!isDeclarationsEmpty()) {
      getDeclarations().forEach(listing::add);
    }
    listing.add(getClassBodyElements());
    if (!isInitializersEmpty()) {
      getInitializers().forEach(listing::add);
    }
    return listing;
  }

  public MethodDeclaration declareConstructor() {
    return declareMethod(void.class, "<init>");
  }

  /** Declare new field. */
  public FieldDeclaration declareField(Class<?> type, String name) {
    return declareField(JavaType.of(type), name);
  }

  /** Declare new field. */
  public FieldDeclaration declareField(JavaType type, String name) {
    FieldDeclaration declaration = new FieldDeclaration();
    declaration.setCompilationUnit(getCompilationUnit().orElse(null));
    declaration.setEnclosingDeclaration(this);
    declaration.setType(type);
    declaration.setName(name);
    getClassBodyElements().add(declaration);
    return declaration;
  }

  /** Declare new initializer block. */
  public Initializer declareInitializer(boolean staticInitializer) {
    Initializer initializer = new Initializer();
    initializer.setEnclosing(this);
    initializer.setStatic(staticInitializer);
    getInitializers().add(initializer);
    return initializer;
  }

  /** Declare new method. */
  public MethodDeclaration declareMethod(Class<?> type, String name) {
    return declareMethod(JavaType.of(type), name);
  }

  /** Declare new method. */
  public MethodDeclaration declareMethod(JavaType type, String name) {
    MethodDeclaration declaration = new MethodDeclaration();
    declaration.setCompilationUnit(getCompilationUnit().orElse(null));
    declaration.setEnclosingDeclaration(this);
    declaration.setReturnType(type);
    declaration.setName(name);
    getClassBodyElements().add(declaration);
    return declaration;
  }

  public List<Listable> getClassBodyElements() {
    return classBodyElements;
  }

  public List<Initializer> getInitializers() {
    if (initializers == Collections.EMPTY_LIST) {
      initializers = new ArrayList<>();
    }
    return initializers;
  }

  public List<ClassType> getInterfaces() {
    if (interfaces == Collections.EMPTY_LIST) {
      interfaces = new ArrayList<>();
    }
    return interfaces;
  }

  public boolean isBodyEmpty() {
    return isDeclarationsEmpty() && isInitializersEmpty() && getClassBodyElements().isEmpty();
  }

  public boolean isInitializersEmpty() {
    return initializers.isEmpty();
  }

  public boolean isInterfacesEmpty() {
    return interfaces.isEmpty();
  }

  public boolean isLocal() {
    return local;
  }

  public void setLocal(boolean local) {
    this.local = local;
  }
}
