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

import com.github.sormuras.listing.Listing;

/**
 * An interface declaration introduces a new reference type whose members are classes, interfaces,
 * constants, and methods. This type has no instance variables, and typically declares one or more
 * abstract methods; otherwise unrelated classes can implement the interface by providing
 * implementations for its abstract methods. Interfaces may not be directly instantiated.
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.1
 */
public class InterfaceDeclaration extends TypeDeclaration {

  @Override
  public Listing apply(Listing listing) {
    listing.newline();
    listing.add(toAnnotationsListable());
    listing.add(toModifiersListable());
    listing.add("interface").add(' ').add(getName());
    listing.add(' ').add('{').newline();
    listing.indent(1);
    if (!isDeclarationsEmpty()) {
      getDeclarations().forEach(listing::add);
    }
    listing.indent(-1).add('}').newline();
    return listing;
  }
}
