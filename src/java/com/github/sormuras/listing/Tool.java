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

import static java.lang.Character.isISOControl;
import static java.util.Collections.addAll;
import static java.util.Objects.requireNonNull;

import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Modifier;

/** Common tools. */
public interface Tool {

  static void assume(boolean condition, String format, Object... args) {
    if (condition) {
      return;
    }
    throw new AssertionError(String.format(format, args));
  }

  /**
   * Creates a joined name string for the given components.
   *
   * @param packageName the name of the package, can be empty
   * @param names the list of names, can be empty
   * @return all names joined to a single string
   */
  static String canonical(String packageName, List<String> names) {
    requireNonNull(packageName, "packageName");
    requireNonNull(names, "names");
    if (packageName.isEmpty() && names.isEmpty()) {
      throw new AssertionError("packageName and names are empty");
    }
    if (names.isEmpty()) {
      return packageName;
    }
    StringBuilder builder = new StringBuilder();
    if (!packageName.isEmpty()) {
      builder.append(packageName).append(".");
    }
    if (names.size() == 1) {
      return builder.append(names.get(0)).toString();
    }
    return builder.append(String.join(".", names)).toString();
  }

  /** Return element type of the a class member. */
  static ElementType elementOf(Member member) {
    if (member instanceof Constructor) {
      return ElementType.CONSTRUCTOR;
    }
    if (member instanceof Field) {
      return ElementType.FIELD;
    }
    if (member instanceof Method) {
      return ElementType.METHOD;
    }
    throw new AssertionError("unexpected member: " + member);
  }

  /**
   * Escape Sequences for Character and String Literals.
   *
   * <p>The character and string escape sequences allow for the representation of some nongraphic
   * characters without using Unicode escapes, as well as the single quote, double quote, and
   * backslash characters, in character literals (§3.10.4) and string literals (§3.10.5).
   *
   * <p>https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.10.6
   */
  static String escape(char character) {
    switch (character) {
      case '\b': /* \u0008: backspace (BS) */
        return "\\b";
      case '\t': /* \u0009: horizontal tab (HT) */
        return "\\t";
      case '\n': /* \u000a: linefeed (LF) */
        return "\\n";
      case '\f': /* \u000c: form feed (FF) */
        return "\\f";
      case '\r': /* \u000d: carriage return (CR) */
        return "\\r";
      case '\"': /* \u0022: double quote (") */
        return "\"";
      case '\'': /* \u0027: single quote (') */
        return "\\'";
      case '\\': /* \u005c: backslash (\) */
        return "\\\\";
      default:
        return isISOControl(character)
            ? String.format("\\u%04x", (int) character)
            : Character.toString(character);
    }
  }

  /** Returns the string literal representing {@code value}, including wrapping double quotes. */
  static String escape(String value) {
    StringBuilder result = new StringBuilder(value.length() + 2);
    result.append('"');
    for (int i = 0; i < value.length(); i++) {
      char character = value.charAt(i);
      // trivial case: single quote must not be escaped
      if (character == '\'') {
        result.append("'");
        continue;
      }
      // trivial case: double quotes must be escaped
      if (character == '\"') {
        result.append("\\\"");
        continue;
      }
      // default case: just let character escaper do its work
      result.append(escape(character));
    }
    result.append('"');
    return result.toString();
  }

  /** Convert an integer consisting of modification bits into a set of {@link Modifier}s. */
  static Set<Modifier> modifiers(int mod) {
    Set<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
    if (java.lang.reflect.Modifier.isAbstract(mod)) {
      modifiers.add(Modifier.ABSTRACT);
    }
    if (java.lang.reflect.Modifier.isFinal(mod)) {
      modifiers.add(Modifier.FINAL);
    }
    if (java.lang.reflect.Modifier.isNative(mod)) {
      modifiers.add(Modifier.NATIVE);
    }
    if (java.lang.reflect.Modifier.isPrivate(mod)) {
      modifiers.add(Modifier.PRIVATE);
    }
    if (java.lang.reflect.Modifier.isProtected(mod)) {
      modifiers.add(Modifier.PROTECTED);
    }
    if (java.lang.reflect.Modifier.isPublic(mod)) {
      modifiers.add(Modifier.PUBLIC);
    }
    if (java.lang.reflect.Modifier.isStatic(mod)) {
      modifiers.add(Modifier.STATIC);
    }
    if (java.lang.reflect.Modifier.isStrict(mod)) {
      modifiers.add(Modifier.STRICTFP);
    }
    if (java.lang.reflect.Modifier.isSynchronized(mod)) {
      modifiers.add(Modifier.SYNCHRONIZED);
    }
    if (java.lang.reflect.Modifier.isTransient(mod)) {
      modifiers.add(Modifier.TRANSIENT);
    }
    if (java.lang.reflect.Modifier.isVolatile(mod)) {
      modifiers.add(Modifier.VOLATILE);
    }
    return modifiers;
  }

  /** Return package name of the class as a String. */
  static String packageOf(Class<?> type) {
    requireNonNull(type, "type");
    // trivial case: package is attached to the type
    Package packageOfType = type.getPackage();
    if (packageOfType != null) {
      return packageOfType.getName();
    }
    // trivial case: no package by definition
    if (type.isArray() || type.isPrimitive()) { // || type.isAnonymousClass()) {
      // TODO investigate type.isLocalClass() || type.isMemberClass() || type.isSynthetic()
      return "";
    }
    return packageOf(type.getCanonicalName());
  }

  /** Find last dot {@code '.'} and return first part. */
  static String packageOf(String typeName) {
    String name = typeName;
    int lastDot = name.lastIndexOf('.');
    if (lastDot == -1) {
      return "";
    }
    name = name.substring(0, lastDot);
    if (name.isEmpty()) {
      throw new AssertionError("empty package name of type named: " + typeName);
    }
    return name;
  }

  /** Return all simple names of the given class as a list of Strings. */
  static List<String> simpleNames(Class<?> type, String... additionalNames) {
    requireNonNull(type, "type");
    List<String> names = new LinkedList<>();
    while (type != null) {
      names.add(0, type.getSimpleName());
      type = type.getEnclosingClass();
    }
    addAll(names, additionalNames);
    return names;
  }
}
