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
 * <p>
 * A declared entity (§6.1) is a package, class type (normal or enum), interface type (normal or
 * annotation type), member (class, interface, field, or method) of a reference type, type parameter
 * (of a class, interface, method or constructor), parameter (to a method, constructor, or exception
 * handler), or local variable.
 * <p>
 * Names in programs are either simple, consisting of a single identifier, or qualified, consisting
 * of a sequence of identifiers separated by "." tokens (§6.2).
 * <p>
 * JavaName also stores an optional set of modifiers and an optional target element type describing
 * the purpose of the name.
 *
 * @author Christian Stein
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-6.html
 */
public class JavaName implements Listable {

  public static JavaName of(Class<?> type) {
    Tool.check(type, "type");
    String packageName = Tool.packageOf(type);
    List<String> names = Tool.simpleNames(type);
    return new JavaName(packageName, names)
        .setTarget(ElementType.TYPE)
        .setModifiers(type.getModifiers());
  }

  public static JavaName of(Enum<?> constant) {
    Tool.check(constant, "constant");
    String packageName = Tool.packageOf(constant.getDeclaringClass());
    List<String> names = Tool.simpleNames(constant.getDeclaringClass(), constant.name());
    return new JavaName(packageName, names)
        .setTarget(ElementType.FIELD)
        .setModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
  }

  public static JavaName of(Member member) {
    Tool.check(member, "member");
    Class<?> type = member.getDeclaringClass();
    String packageName = Tool.packageOf(type);
    List<String> names = Tool.simpleNames(type, member.getName());
    return new JavaName(packageName, names)
        .setTarget(Tool.elementOf(member))
        .setModifiers(member.getModifiers());
  }

  public static JavaName of(String... names) {
    return of(null, names);
  }

  public static JavaName of(Modifier modifier, String... names) {
    Tool.assume(names.length > 0, "non-empty names array expected");
    List<String> simpleNames = new ArrayList<>(Arrays.asList(names));
    Iterator<String> iterator = simpleNames.iterator();
    String packageName = iterator.next();
    iterator.remove();
    simpleNames.forEach(n -> Tool.assume(SourceVersion.isIdentifier(n), "non identifier %s", n));
    JavaName javaName = new JavaName(packageName, simpleNames);
    if (names.length == 1) javaName.setTarget(ElementType.PACKAGE);
    if (names.length == 2) javaName.setTarget(ElementType.TYPE);
    if (modifier != null) javaName.setModifiers(modifier);
    return javaName;
  }

  private final String canonicalName;
  private final Set<Modifier> modifiers;
  private final String packageName;
  private final List<String> simpleNames;
  private ElementType target;

  protected JavaName(String packageName, List<String> simpleNames) {
    this.packageName = Tool.check(packageName, "packageName");
    this.simpleNames = unmodifiableList(Tool.check(simpleNames, "simpleNames"));
    this.modifiers = EnumSet.noneOf(Modifier.class);
    this.canonicalName = Tool.canonical(packageName, simpleNames);
    this.target = null;
  }

  @Override
  public Listing apply(Listing listing) {
    return listing.add(this);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    return hashCode() == other.hashCode();
  }

  /** {@code "java.lang.Thread.State"} */
  public String getCanonicalName() {
    return canonicalName;
  }

  public Optional<JavaName> getEnclosing() {
    if (isEnclosed()) {
      List<String> names = getSimpleNames().subList(0, getSimpleNames().size() - 1);
      return Optional.of(new JavaName(getPackageName(), names));
    }
    return Optional.empty();
  }

  public Set<Modifier> getModifiers() {
    return modifiers;
  }

  /** {@code "java.lang"} */
  public String getPackageName() {
    return packageName;
  }

  /** {@code ["Thread", "State"]} */
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

  public boolean isLocatedInJavaLangPackage() {
    return "java.lang".equals(packageName);
  }

  public boolean isStatic() {
    return getModifiers().contains(Modifier.STATIC);
  }

  public JavaName setModifiers(int mod) {
    this.modifiers.clear();
    this.modifiers.addAll(Tool.modifiers(mod));
    return this;
  }

  public JavaName setModifiers(Modifier... modifiers) {
    this.modifiers.clear();
    Collections.addAll(this.modifiers, modifiers);
    return this;
  }

  public JavaName setTarget(ElementType target) {
    this.target = target;
    return this;
  }

  @Override
  public String toString() {
    return "JavaName{"
        + getCanonicalName()
        + ", target="
        + getTarget()
        + ", modifiers="
        + getModifiers()
        + "}";
  }
}
