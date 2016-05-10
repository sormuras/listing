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
 * PackageDeclaration:<br>
 * {PackageModifier} package Identifier {. Identifier} ;
 *
 * @author Christian Stein
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.4
 */
public class PackageDeclaration implements Listable, Annotated<PackageDeclaration> {

  private final List<JavaAnnotation> annotations = new ArrayList<>();
  private final JavaName packageName;

  /**
   * Unnamed package declaration constructor - use it sparsely.
   */
  public PackageDeclaration() {
    this.packageName = null;
  }

  public PackageDeclaration(JavaName packageName) {
    this.packageName = packageName;
  }

  public PackageDeclaration(String packageName) {
    this(JavaName.of(packageName));
  }

  @Override
  public Listing apply(Listing listing) {
    if (isUnnamed()) {
      return listing;
    }
    String name = packageName.getPackageName();
    return listing.add(toAnnotationsListable()).add("package ").add(name).add(';').newline();
  }

  @Override
  public List<JavaAnnotation> getAnnotations() {
    return annotations;
  }

  @Override
  public ElementType getAnnotationTarget() {
    return ElementType.PACKAGE;
  }

  public boolean isUnnamed() {
    return packageName == null;
  }
}
