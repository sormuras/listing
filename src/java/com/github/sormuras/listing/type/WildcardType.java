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

import com.github.sormuras.listing.Annotated;
import com.github.sormuras.listing.Listing;
import java.lang.annotation.ElementType;
import java.util.Optional;

/**
 * Wildcards are useful in situations where only partial knowledge about the type parameter is
 * required.
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-Wildcard
 */
public class WildcardType extends Annotated implements JavaType {

  private ReferenceType boundExtends = ClassType.of(Object.class);
  private ReferenceType boundSuper = null;

  @Override
  public Listing apply(Listing listing) {
    listing.add(toAnnotationsListable());
    listing.add('?');
    if (!getBoundExtends().isJavaLangObject()) {
      return listing.add(" extends ").add(getBoundExtends());
    }
    if (getBoundSuper().isPresent()) {
      return listing.add(" super ").add(getBoundSuper().get());
    }
    return listing;
  }

  @Override
  public ElementType getAnnotationTarget() {
    return ElementType.TYPE_PARAMETER;
  }

  public ReferenceType getBoundExtends() {
    return boundExtends;
  }

  public Optional<ReferenceType> getBoundSuper() {
    return Optional.ofNullable(boundSuper);
  }

  /** Set upper bound, read {@code extends}, type. */
  public void setBoundExtends(ReferenceType boundExtends) {
    this.boundExtends = boundExtends;
    this.boundSuper = null;
  }

  /** Set lower bound, read {@code super}, type. */
  public void setBoundSuper(ReferenceType boundSuper) {
    this.boundExtends = ClassType.of(Object.class);
    this.boundSuper = boundSuper;
  }
}
