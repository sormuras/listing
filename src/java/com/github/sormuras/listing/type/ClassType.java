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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ClassType extends ReferenceType {

  public static ClassType of(Class<?> type) {
    return of(Name.of(type));
  }

  public static ClassType of(Class<?> type, Class<?>... arguments) {
    TypeArgument[] args = stream(arguments).map(TypeArgument::of).toArray(TypeArgument[]::new);
    return of(Name.of(type), args);
  }

  /** Create class type for name and optional type arguments. */
  public static ClassType of(Name name, TypeArgument... typeArguments) {
    ClassType classType = new ClassType();
    classType.setPackageName(name.getPackageName());
    classType
        .getNames()
        .addAll(name.getSimpleNames().stream().map(ClassName::of).collect(toList()));
    Collections.addAll(classType.getTypeArguments(), typeArguments);
    return classType;
  }

  public static ClassType of(String... names) {
    return of(Name.of(names));
  }

  public static ClassType of(String packageName, ClassName... names) {
    ClassType classType = new ClassType();
    classType.setPackageName(packageName);
    classType.getNames().addAll(Arrays.asList(names));
    return classType;
  }

  private final List<ClassName> names = new ArrayList<>();
  private String packageName = "";

  @Override
  public Listing apply(Listing listing) {
    Name name = getName();
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
    return Optional.of(of(getName().getEnclosing().get()));
  }

  public Name getName() {
    List<String> simpleNames = new ArrayList<>();
    names.forEach(n -> simpleNames.add(n.getName()));
    return new Name(getPackageName(), simpleNames);
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

  public void setPackageName(String packageName) {
    this.packageName = packageName;
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
