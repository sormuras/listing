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

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.TreeSet;

/**
 * An import declaration allows a named type or a static member to be referred to by a simple name
 * (§6.2) that consists of a single identifier.
 *
 * <li>https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.5
 */
public class ImportDeclarations implements Listable {

  private Set<JavaName> onDemandStaticImports = new TreeSet<>();
  private Set<JavaName> onDemandTypeImports = new TreeSet<>();
  private Set<JavaName> singleStaticImports = new TreeSet<>();
  private Set<JavaName> singleTypeImports = new TreeSet<>();

  /**
   * Single static (enum) import declaration.
   *
   * <li>{@code import static java.lang.Thread.State.NEW;}
   * <li>https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.5.3
   */
  public ImportDeclarations addSingleStaticImport(Enum<?> constant) {
    Tool.check(constant, "constant");
    singleStaticImports.add(JavaName.of(constant));
    return this;
  }

  /**
   * Single static (member) import declaration.
   *
   * <li>{@code import static java.util.Collections.shuffle;}
   * <li>https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.5.3
   */
  public ImportDeclarations addSingleStaticImport(JavaName name) {
    Tool.check(name, "name");
    Tool.assume(name.isStatic(), "%s must be static", name);
    singleStaticImports.add(name);
    return this;
  }

  /**
   * Single static (member) import declaration.
   *
   * <li>{@code import static java.util.Collections.shuffle;}
   * <li>https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.5.3
   */
  public ImportDeclarations addSingleStaticImport(Member member) {
    Tool.check(member, "member");
    Tool.assume(Modifier.isStatic(member.getModifiers()), "%s must be static", member);
    singleStaticImports.add(JavaName.of(member));
    return this;
  }

  /**
   * Single type.
   *
   * <li>{@code import java.util.Collections;}
   *
   * <li>https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.5.1
   */
  public ImportDeclarations addSingleTypeImport(Class<?> type) {
    return addSingleTypeImport(JavaName.of(type));
  }

  /**
   * A single-type-import declaration imports a single type by giving its canonical name, making it
   * available under a simple name in the class and interface declarations of the compilation unit
   * in which the single-type-import declaration appears.
   *
   * <li>{@code import java.util.Collections;}
   *
   * <li>https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.5.1
   */
  public ImportDeclarations addSingleTypeImport(JavaName typeName) {
    singleTypeImports.add(typeName);
    return this;
  }

  /**
   * A <i>static-import-on-demand</i> declaration allows all accessible <b>static</b> members of a
   * named type to be imported as needed.
   *
   * <li>{@code import static java.util.Collections.*;}
   * <li>https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.5.4
   */
  public ImportDeclarations addStaticImportOnDemand(JavaName typeName) {
    onDemandStaticImports.add(typeName);
    return this;
  }

  /**
   * A <i>type-import-on-demand</i> declaration allows all accessible types of a named package or
   * type to be imported as needed.
   *
   * <li>{@code import java.util.*;}
   * <li>https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.5.2
   */
  public ImportDeclarations addTypeImportOnDemand(JavaName packageOrTypeName) {
    onDemandTypeImports.add(packageOrTypeName);
    return this;
  }

  @Override
  public Listing apply(Listing listing) {
    if (!onDemandStaticImports.isEmpty()) {
      onDemandStaticImports.forEach(
          n -> listing.add("import static ").add(n.getCanonicalName()).add(".*;").newline());
    }
    if (!singleStaticImports.isEmpty()) {
      singleStaticImports.forEach(
          n -> listing.add("import static ").add(n.getCanonicalName()).add(';').newline());
    }
    if (!onDemandTypeImports.isEmpty()) {
      onDemandTypeImports.forEach(
          n -> listing.add("import ").add(n.getCanonicalName()).add(".*;").newline());
    }
    if (!singleTypeImports.isEmpty()) {
      singleTypeImports.forEach(
          n -> listing.add("import ").add(n.getCanonicalName()).add(';').newline());
    }
    return listing;
  }

  @Override
  public boolean isEmpty() {
    return singleTypeImports.isEmpty()
        && onDemandTypeImports.isEmpty()
        && singleStaticImports.isEmpty()
        && onDemandStaticImports.isEmpty();
  }
}
