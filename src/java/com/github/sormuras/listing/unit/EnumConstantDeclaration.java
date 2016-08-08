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
import java.lang.annotation.ElementType;

/**
 * An enum constant defines an instance of the enum type.
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.9.1
 */
public class EnumConstantDeclaration extends AbstractAnnotatable implements Listable {

  private Listable arguments;
  private NormalClassDeclaration body;
  private String name;

  @Override
  public Listing apply(Listing listing) {
    listing.newline();
    // {EnumConstantModifier}
    listing.add(toAnnotationsListable());
    // Identifier
    listing.add(getName());
    // [( [ArgumentList] )]
    if (arguments != null) {
      listing.add('(');
      listing.add(getArguments());
      listing.add(')');
    }
    // [ClassBody]
    if (getBody() != null) {
      getBody().applyClassBody(listing);
    }
    return listing;
  }

  @Override
  public ElementType getAnnotationTarget() {
    return ElementType.TYPE;
  }

  public Listable getArguments() {
    return arguments;
  }

  public NormalClassDeclaration getBody() {
    return body;
  }

  public String getName() {
    return name;
  }

  public void setArguments(Listable arguments) {
    this.arguments = arguments;
  }

  public void setBody(NormalClassDeclaration body) {
    this.body = body;
  }

  public EnumConstantDeclaration setName(String name) {
    this.name = name;
    return this;
  }
}