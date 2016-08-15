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

import com.github.sormuras.listing.Listable;
import com.github.sormuras.listing.Listing;

/**
 * Type arguments may be either reference types or wildcards.
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.5.1
 */
public class TypeArgument implements Listable {

  public static TypeArgument of(Class<?> argument) {
    return of(JavaType.of(argument));
  }

  /** Initializes this {@link TypeArgument} instance. */
  public static TypeArgument of(JavaType argument) {
    TypeArgument typeArgument = new TypeArgument();
    if (argument instanceof WildcardType) {
      typeArgument.setWildcard((WildcardType) argument);
      return typeArgument;
    }
    if (argument instanceof ReferenceType) {
      typeArgument.setReference((ReferenceType) argument);
      return typeArgument;
    }
    throw new AssertionError("neither reference nor wildcard: " + argument);
  }

  private ReferenceType reference;
  private WildcardType wildcard;

  @Override
  public Listing apply(Listing listing) {
    if (reference == null) {
      return listing.add(wildcard);
    }
    return listing.add(reference);
  }

  public ReferenceType getReference() {
    return reference;
  }

  public WildcardType getWildcard() {
    return wildcard;
  }

  public void setReference(ReferenceType reference) {
    this.reference = reference;
    this.wildcard = null;
  }

  public void setWildcard(WildcardType wildcard) {
    this.wildcard = wildcard;
    this.reference = null;
  }
}
