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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * A block is a sequence of statements, local class declarations and local variable declaration
 * statements within braces.
 *
 * <p>A local class is a nested class that is not a member of any class and that has a name.
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-14.html#jls-14.2
 */
public class Block implements Listable {

  // { LocalVariableDeclarationStatement, Statement, ClassDeclaration }
  private List<Listable> sequence = new ArrayList<>();

  public Block add(Listable listable) {
    sequence.add(listable);
    return this;
  }

  public Block add(String... lines) {
    for (String line : lines) {
      sequence.add(l -> l.add(line).newline());
    }
    return this;
  }

  @Override
  public Listing apply(Listing listing) {
    listing.add('{').newline().indent(1);
    getSequence().forEach(listing::add);
    listing.indent(-1).add('}').newline();
    return listing;
  }

  /**
   * Declare an enum that is not a member of any class and that has a name.
   *
   * @param name the simple local enum name
   * @return enum declaration instance
   */
  public EnumDeclaration declareLocalEnum(String name) {
    return declareLocal(EnumDeclaration::new, name);
  }

  /**
   * Declare a normal local class that is not a member of any class and that has a name.
   *
   * @param name the simple local class name
   * @return normal class declaration instance
   */
  public NormalClassDeclaration declareLocalClass(String name) {
    return declareLocal(NormalClassDeclaration::new, name);
  }

  public <C extends ClassDeclaration> C declareLocal(Supplier<C> supplier, String name) {
    C declaration = supplier.get();
    declaration.setLocal(true);
    declaration.setName(name);
    getSequence().add(declaration);
    return declaration;
  }

  public List<Listable> getSequence() {
    return sequence;
  }
}
