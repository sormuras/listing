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
import com.github.sormuras.listing.type.JavaType;
import java.lang.annotation.ElementType;

/**
 * The body of an annotation type declaration may contain method declarations, each of which defines
 * an <b>element</b> of the annotation type. An annotation type has no elements other than those
 * defined by the methods it explicitly declares.
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.6.1
 */
public class AnnotationElement extends NamedMember {

  private Listable defaultValue;
  private JavaType returnType;

  @Override
  public Listing apply(Listing listing) {
    listing.newline();
    listing.add(toAnnotationsListable());
    listing.add(getReturnType()).add(' ').add(getName()).add("()");
    if (defaultValue != null) {
      listing.add(' ').add("default").add(' ').add(getDefaultValue());
    }
    listing.add(';').newline();
    return listing;
  }

  @Override
  public ElementType getAnnotationTarget() {
    return ElementType.METHOD;
  }

  public Listable getDefaultValue() {
    return defaultValue;
  }

  public JavaType getReturnType() {
    return returnType;
  }

  public void setDefaultValue(Listable defaultValue) {
    this.defaultValue = defaultValue;
  }

  public void setReturnType(JavaType type) {
    this.returnType = type;
  }
}
