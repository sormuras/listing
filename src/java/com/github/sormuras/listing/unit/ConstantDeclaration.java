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
import com.github.sormuras.listing.type.JavaType;
import java.lang.annotation.ElementType;

/**
 * Constant field.
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.3
 */
public class ConstantDeclaration extends AbstractAnnotatable implements Listable {

  private Listable initializer;
  private String name;
  private JavaType type;

  @Override
  public Listing apply(Listing listing) {
    listing.newline();
    listing.add(toAnnotationsListable());
    listing.add(getType()).add(' ').add(getName()).add(" = ").add(getInitializer());
    listing.add(';').newline();
    return listing;
  }

  @Override
  public ElementType getAnnotationTarget() {
    return ElementType.FIELD;
  }

  public Listable getInitializer() {
    return initializer;
  }

  public String getName() {
    return name;
  }

  public JavaType getType() {
    return type;
  }

  public ConstantDeclaration setInitializer(Listable initializer) {
    this.initializer = initializer;
    return this;
  }

  public ConstantDeclaration setName(String name) {
    this.name = name;
    return this;
  }

  public ConstantDeclaration setType(JavaType type) {
    this.type = type;
    return this;
  }
}
