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

import static java.util.Objects.requireNonNull;

import com.github.sormuras.listing.Listable;
import com.github.sormuras.listing.Listing;
import com.github.sormuras.listing.Name;
import com.github.sormuras.listing.Tool;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

/**
 * An import declaration allows a named type or a static member to be referred to by a simple name
 * (§6.2) that consists of a single identifier.
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.5
 */
public class ImportDeclarations implements Listable, Predicate<Name> {

  private Set<Name> onDemandStaticImports = new TreeSet<>();
  private Set<Name> onDemandTypeImports = new TreeSet<>();
  private Set<Name> singleStaticImports = new TreeSet<>();
  private Set<Name> singleTypeImports = new TreeSet<>();

  /**
   * Single static (enum) import declaration.
   * <li>{@code import static java.lang.Thread.State.NEW;}
   * <li>https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.5.3
   */
  public ImportDeclarations addSingleStaticImport(Enum<?> constant) {
    singleStaticImports.add(Name.of(requireNonNull(constant, "constant")));
    return this;
  }

  /**
   * Single static (member) import declaration.
   * <li>{@code import static java.util.Collections.shuffle;}
   * <li>https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.5.3
   */
  public ImportDeclarations addSingleStaticImport(Name name) {
    singleStaticImports.add(requireNonNull(name, "name"));
    return this;
  }

  /**
   * Single static (member) import declaration.
   * <li>{@code import static java.util.Collections.shuffle;}
   * <li>https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.5.3
   */
  public ImportDeclarations addSingleStaticImport(Member member) {
    Tool.assume(Modifier.isStatic(member.getModifiers()), "member %s must be static", member);
    singleStaticImports.add(Name.of(member));
    return this;
  }

  /**
   * Single type.
   * <li>{@code import java.util.Collections;}
   * <li>https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.5.1
   */
  public ImportDeclarations addSingleTypeImport(Class<?> type) {
    return addSingleTypeImport(Name.of(type));
  }

  /**
   * A single-type-import declaration imports a single type by giving its canonical name, making it
   * available under a simple name in the class and interface declarations of the compilation unit
   * in which the single-type-import declaration appears.
   * <li>{@code import java.util.Collections;}
   * <li>https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.5.1
   */
  public ImportDeclarations addSingleTypeImport(Name typeName) {
    singleTypeImports.add(typeName);
    return this;
  }

  /**
   * A <i>static-import-on-demand</i> declaration allows all accessible <b>static</b> members of a
   * named type to be imported as needed.
   * <li>{@code import static java.util.Collections.*;}
   * <li>https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.5.4
   */
  public ImportDeclarations addStaticImportOnDemand(Name typeName) {
    onDemandStaticImports.add(typeName);
    return this;
  }

  /**
   * A <i>type-import-on-demand</i> declaration allows all accessible types of a named package or
   * type to be imported as needed.
   * <li>{@code import java.util.*;}
   * <li>https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.5.2
   */
  public ImportDeclarations addTypeImportOnDemand(Name packageOrTypeName) {
    onDemandTypeImports.add(packageOrTypeName);
    return this;
  }

  @Override
  public Listing apply(Listing listing) {
    if (isEmpty()) {
      return listing;
    }
    listing.newline();
    if (!onDemandStaticImports.isEmpty()) {
      onDemandStaticImports.forEach(
          n -> listing.add("import static ").add(n.getCanonicalName()).add(".*;").newline());
    }
    if (!singleStaticImports.isEmpty()) {
      singleStaticImports.forEach(
          n -> listing.add("import static ").add(n.getCanonicalName()).add(';').newline());
    }
    listing.newline();
    if (!onDemandTypeImports.isEmpty()) {
      onDemandTypeImports.forEach(
          n -> listing.add("import ").add(n.getCanonicalName()).add(".*;").newline());
    }
    if (!singleTypeImports.isEmpty()) {
      singleTypeImports.forEach(
          n -> listing.add("import ").add(n.getCanonicalName()).add(';').newline());
    }
    listing.trim();
    return listing;
  }

  @Override
  public boolean isEmpty() {
    return singleTypeImports.isEmpty()
        && onDemandTypeImports.isEmpty()
        && singleStaticImports.isEmpty()
        && onDemandStaticImports.isEmpty();
  }

  @Override
  public boolean test(Name name) {
    // simple 1:1 match with a single (static) import
    if (singleTypeImports.contains(name) || singleStaticImports.contains(name)) {
      return true;
    }
    // on demand test...
    Optional<Name> enclosing = name.getEnclosing();
    if (enclosing.isPresent()) {
      name = enclosing.get();
      if (onDemandStaticImports.contains(name) || onDemandTypeImports.contains(name)) {
        return true;
      }
    }
    return false;
  }
}
