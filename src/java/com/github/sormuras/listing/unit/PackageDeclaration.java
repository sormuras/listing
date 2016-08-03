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

import com.github.sormuras.listing.Annotatable.AbstractAnnotatable;
import com.github.sormuras.listing.Listable;
import com.github.sormuras.listing.Listing;
import com.github.sormuras.listing.Name;
import java.lang.annotation.ElementType;
import java.net.URI;

/**
 * PackageDeclaration:<br>
 * {PackageModifier} package Identifier {. Identifier} ;
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.4
 */
public class PackageDeclaration extends AbstractAnnotatable implements Listable {

  private final Name name;

  /** Unnamed package declaration constructor - use it sparsely. */
  public PackageDeclaration() {
    this.name = null;
  }

  public PackageDeclaration(Name packageName) {
    this.name = packageName;
  }

  public PackageDeclaration(String packageName) {
    this(Name.of(packageName));
  }

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

  public boolean isUnnamed() {
    return getName() == null;
  }

  public String resolve(String simpleName) {
    if (isUnnamed()) {
      return simpleName;
    }
    return getName().getCanonicalName() + '.' + simpleName;
  }

  public URI toUri(String simpleName) {
    if (isUnnamed()) {
      return URI.create(simpleName);
    }
    return URI.create(getName().getCanonicalName().replace('.', '/') + '/' + simpleName);
  }
}
