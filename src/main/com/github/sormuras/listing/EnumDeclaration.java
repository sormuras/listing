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
package com.github.sormuras.listing;

import java.util.ArrayList;
import java.util.List;

import com.github.sormuras.listing.ClassType;
import com.github.sormuras.listing.JavaType;
import com.github.sormuras.listing.Listing;

/**
 * An enum declaration specifies a new enum type, a special kind of class type.
 *
 * @author Christian Stein
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.9
 */
public class EnumDeclaration extends TypeDeclaration<EnumDeclaration> {

  private final List<ClassType> interfaces = new ArrayList<>();

  public EnumDeclaration() {
    super("enum");
  }

  public EnumDeclaration addInterface(ClassType classType) {
    interfaces.add(classType);
    return this;
  }

  public EnumDeclaration addInterface(JavaType<?> interfaceType) {
    return addInterface((ClassType) interfaceType);
  }

  @Override
  protected Listing applyDeclarationHead(Listing listing) {
    super.applyDeclarationHead(listing);
    // [Superinterfaces]
    if (!interfaces.isEmpty()) {
      listing.add(" implements ").add(interfaces, ", ");
    }
    return listing;
  }
}
