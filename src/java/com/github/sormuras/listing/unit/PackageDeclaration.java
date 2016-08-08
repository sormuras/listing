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

import com.github.sormuras.listing.Annotated;
import com.github.sormuras.listing.Listing;
import com.github.sormuras.listing.Name;
import java.lang.annotation.ElementType;
import java.net.URI;

/**
 * Package declaration.
 *
 * <pre>
 * PackageDeclaration:<br>
 * {PackageModifier} package Identifier {. Identifier} ;
 * </pre>
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.4
 */
public class PackageDeclaration extends Annotated {

  public static PackageDeclaration of(Name packageName) {
    PackageDeclaration name = new PackageDeclaration();
    name.setName(packageName);
    return name;
  }

  public static PackageDeclaration of(String packageName) {
    return of(Name.of(packageName));
  }

  private Name name = null;

  @Override
  public Listing apply(Listing listing) {
    if (isUnnamed()) {
      return listing;
    }
    String name = getName().getPackageName();
    return listing.add(toAnnotationsListable()).add("package ").add(name).add(';').newline();
  }

  @Override
  public ElementType getAnnotationTarget() {
    return ElementType.PACKAGE;
  }

  public Name getName() {
    return name;
  }

  @Override
  public boolean isEmpty() {
    return isUnnamed();
  }

  public boolean isUnnamed() {
    return getName() == null;
  }

  public String resolve(String simpleName) {
    if (isUnnamed()) {
      return simpleName;
    }
    return getName().getCanonicalName() + '.' + simpleName;
  }

  public void setName(Name name) {
    this.name = name;
  }

  public URI toUri(String simpleName) {
    if (isUnnamed()) {
      return URI.create(simpleName);
    }
    return URI.create(getName().getCanonicalName().replace('.', '/') + '/' + simpleName);
  }
}
