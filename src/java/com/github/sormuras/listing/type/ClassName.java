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

package com.github.sormuras.listing.type;

import com.github.sormuras.listing.Annotatable;
import com.github.sormuras.listing.Annotation;
import com.github.sormuras.listing.Listable;
import com.github.sormuras.listing.Listing;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Simple and(!) annotatable and(!) typed class or interface name. */
public class ClassName implements Annotatable, Listable {

  public static ClassName of(String name) {
    ClassName className = new ClassName();
    className.setName(name);
    return className;
  }

  private List<Annotation> annotations = Collections.emptyList();
  private String name;
  private List<TypeArgument> typeArguments = Collections.emptyList();

  @Override
  public Listing apply(Listing listing) {
    listing.add(toAnnotationsListable());
    if (typeArguments.isEmpty()) {
      return listing.add(getName());
    }
    listing.add(getName()).add('<').add(getTypeArguments(), ", ").add('>');
    return listing;
  }

  @Override
  public List<Annotation> getAnnotations() {
    if (annotations == Collections.EMPTY_LIST) {
      annotations = new ArrayList<>();
    }
    return annotations;
  }

  @Override
  public ElementType getAnnotationTarget() {
    return ElementType.TYPE_USE;
  }

  public String getName() {
    return name;
  }

  public List<TypeArgument> getTypeArguments() {
    if (typeArguments == Collections.EMPTY_LIST) {
      typeArguments = new ArrayList<>();
    }
    return typeArguments;
  }

  @Override
  public boolean isAnnotated() {
    return !annotations.isEmpty();
  }

  public void setName(String name) {
    this.name = name;
  }
}
