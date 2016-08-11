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

package com.github.sormuras.listing.type;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import com.github.sormuras.listing.Annotation;
import com.github.sormuras.listing.Listing;
import com.github.sormuras.listing.Name;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ClassType extends ReferenceType {

  public static ClassType of(Class<?> type) {
    return new ClassType(Name.of(type));
  }

  public static ClassType of(Class<?> type, Class<?>... arguments) {
    TypeArgument[] args = stream(arguments).map(TypeArgument::new).toArray(TypeArgument[]::new);
    return new ClassType(Name.of(type), args);
  }

  public static ClassType of(String... names) {
    return new ClassType(Name.of(names));
  }

  private final List<ClassName> names;
  private final String packageName;
  private final Name name;

  /** Initialize this ClassType instance. */
  public ClassType(Name name, TypeArgument... typeArguments) {
    this.name = name;
    this.packageName = name.getPackageName();
    this.names = name.getSimpleNames().stream().map(ClassName::of).collect(toList());
    if (names.isEmpty()) {
      throw new IllegalArgumentException("Not a single class name given by: " + name);
    }
    Collections.addAll(getTypeArguments(), typeArguments);
  }

  @Override
  public Listing apply(Listing listing) {
    boolean skipPackageName = getPackageName().isEmpty();
    skipPackageName |= listing.getImported().test(name);
    skipPackageName |= listing.isOmitJavaLangPackage() && name.isJavaLangPackage();
    if (!skipPackageName) {
      listing.add(getPackageName()).add('.');
    }
    return listing.add(getNames(), ".");
  }

  @Override
  public List<Annotation> getAnnotations() {
    return names.get(names.size() - 1).getAnnotations();
  }

  public Optional<ClassType> getEnclosingClassType() {
    if (names.size() == 1) {
      return Optional.empty();
    }
    return Optional.of(new ClassType(getName().getEnclosing().get()));
  }

  public Name getName() {
    return name;
  }

  public List<ClassName> getNames() {
    return names;
  }

  public String getPackageName() {
    return packageName;
  }

  public List<TypeArgument> getTypeArguments() {
    return names.get(names.size() - 1).getTypeArguments();
  }

  @Override
  public boolean isAnnotated() {
    return names.get(names.size() - 1).isAnnotated();
  }

  @Override
  public boolean isJavaLangObject() {
    return getName().isJavaLangObject();
  }

  @Override
  public String toClassName() {
    StringBuilder builder = new StringBuilder();
    if (!getPackageName().isEmpty()) {
      builder.append(getPackageName()).append('.');
    }
    builder.append(getNames().stream().map(n -> n.getName()).collect(joining("$")));
    return builder.toString();
  }
}
