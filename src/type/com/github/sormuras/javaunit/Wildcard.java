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
 * Wildcards are useful in situations where only partial knowledge about the type parameter is
 * required.
 *
 * @author Christian Stein
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-Wildcard
 */
public class Wildcard extends JavaType<Wildcard> {

  private final List<JavaAnnotation> annotations = new ArrayList<>();
  private ReferenceType<?> boundExtends = new ClassType(Object.class);
  private ReferenceType<?> boundSuper = null;

  @Override
  public List<JavaAnnotation> getAnnotations() {
    return annotations;
  }

  @Override
  public ElementType getAnnotationTarget() {
    return ElementType.TYPE_PARAMETER;
  }

  public ReferenceType<?> getBoundExtends() {
    return boundExtends;
  }

  public ReferenceType<?> getBoundSuper() {
    return boundSuper;
  }

  @Override
  public Listing apply(Listing listing) {
    listing.add(toAnnotationsListable());
    listing.add('?');
    if (!getBoundExtends().isJavaLangObject()) {
      return listing.add(" extends ").add(getBoundExtends());
    }
    if (getBoundSuper() != null) {
      return listing.add(" super ").add(getBoundSuper());
    }
    return listing;
  }

  /** upper bound */
  public Wildcard setBoundExtends(ReferenceType<?> boundExtends) {
    this.boundExtends = boundExtends;
    this.boundSuper = null;
    return this;
  }

  /** lower bound */
  public Wildcard setBoundSuper(ReferenceType<?> boundSuper) {
    this.boundExtends = new ClassType(Object.class);
    this.boundSuper = boundSuper;
    return this;
  }
}
