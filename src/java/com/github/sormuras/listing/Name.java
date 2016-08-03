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

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

import java.lang.annotation.ElementType;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;

/**
 * Names are used to refer to entities declared in a program.
 *
 * <p>A declared entity (§6.1) is a package, class type (normal or enum), interface type (normal or
 * annotation type), member (class, interface, field, or method) of a reference type, type parameter
 * (of a class, interface, method or constructor), parameter (to a method, constructor, or exception
 * handler), or local variable.
 *
 * <p>Names in programs are either simple, consisting of a single identifier, or qualified,
 * consisting of a sequence of identifiers separated by "." tokens (§6.2).
 *
 * <p>JavaName also stores an optional set of modifiers and an optional target element type
 * describing the purpose of the name.
 *
 * @see JLS: <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-6.html">Names</a>
 */
public class Name implements Listable, Modifiable {

  /** Create new Name based on the class type. */
  public static Name of(Class<?> type) {
    requireNonNull(type, "type");
    String packageName = Tool.packageOf(type);
    List<String> names = Tool.simpleNames(type);
    Name name = new Name(packageName, names);
    name.setTarget(ElementType.TYPE);
    name.setModifiers(type.getModifiers());
    return name;
  }

  /** Create new Name based on the enum constant. */
  public static Name of(Enum<?> constant) {
    requireNonNull(constant, "constant");
    String packageName = Tool.packageOf(constant.getDeclaringClass());
    List<String> names = Tool.simpleNames(constant.getDeclaringClass(), constant.name());
    Name name = new Name(packageName, names);
    name.setTarget(ElementType.FIELD); // Field declaration (includes enum constants)
    name.setModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
    return name;
  }

  /** Create new Name based on the member instance. */
  public static Name of(Member member) {
    requireNonNull(member, "member");
    Class<?> type = member.getDeclaringClass();
    String packageName = Tool.packageOf(type);
    List<String> names = Tool.simpleNames(type, member.getName());
    Name name = new Name(packageName, names);
    name.setTarget(Tool.elementOf(member));
    name.setModifiers(member.getModifiers());
    return name;
  }

  /** Create new Name based on the array of Strings, with the first denoting the package name. */
  public static Name of(String... names) {
    requireNonNull(names, "names");
    if (names.length == 0) {
      throw new IllegalArgumentException("non-empty names array expected");
    }
    List<String> simples = new ArrayList<>(Arrays.asList(names));
    Iterator<String> iterator = simples.iterator();
    String packageName = iterator.next();
    iterator.remove();
    if (!simples.stream().allMatch(SourceVersion::isIdentifier)) {
      throw new IllegalArgumentException("non-name in: " + simples);
    }
    Name name = new Name(packageName, simples);
    if (names.length == 1) {
      name.setTarget(ElementType.PACKAGE);
    } else if (names.length == 2) {
      name.setTarget(ElementType.TYPE);
    }
    return name;
  }

  private final String canonicalName;
  private Set<Modifier> modifiers;
  private final String packageName;
  private final List<String> simpleNames;
  private ElementType target;

  public Name(String packageName, List<String> simpleNames) {
    this.packageName = requireNonNull(packageName, "packageName");
    this.simpleNames = unmodifiableList(requireNonNull(simpleNames, "simpleNames"));
    this.modifiers = Collections.emptySet();
    this.canonicalName = Tool.canonical(packageName, simpleNames);
    this.target = null;
  }

  @Override
  public Listing apply(Listing listing) {
    return listing.add(this);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    return hashCode() == other.hashCode();
  }

  /**
   * Return the canonical name as a String.
   *
   * @return For example: {@code "java.lang.Thread.State"}
   */
  public String getCanonicalName() {
    return canonicalName;
  }

  public Optional<Name> getEnclosing() {
    if (isEnclosed()) {
      List<String> names = getSimpleNames().subList(0, getSimpleNames().size() - 1);
      return Optional.of(new Name(getPackageName(), names));
    }
    return Optional.empty();
  }

  @Override
  public Set<Modifier> getModifiers(boolean readonly) {
    if (!readonly && modifiers == Collections.EMPTY_SET) {
      modifiers = EnumSet.noneOf(Modifier.class);
    }
    return modifiers;
  }

  /**
   * Return package name as a String.
   *
   * @return For example: {@code "java.lang"}
   */
  public String getPackageName() {
    return packageName;
  }

  /**
   * Return list of simple names as Strings.
   *
   * @return For example: {@code ["Thread", "State"]}
   */
  public List<String> getSimpleNames() {
    return simpleNames;
  }

  public Optional<ElementType> getTarget() {
    return Optional.ofNullable(target);
  }

  @Override
  public int hashCode() {
    return canonicalName.hashCode();
  }

  public boolean isEnclosed() {
    return getSimpleNames().size() > 1;
  }

  public boolean isJavaLangObject() {
    return "java.lang.Object".equals(canonicalName);
  }

  public boolean isJavaLangPackage() {
    return "java.lang".equals(packageName);
  }

  public Name setTarget(ElementType target) {
    this.target = target;
    return this;
  }

  @Override
  public String toString() {
    return "Name{"
        + getCanonicalName()
        + ", target="
        + getTarget()
        + ", modifiers="
        + getModifiers()
        + "}";
  }
}
