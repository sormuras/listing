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

import static java.util.stream.Collectors.toList;

import java.lang.annotation.ElementType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ClassType extends ReferenceType<ClassType> {

  private List<ClassName> names;
  private final String packageName;
  private final JavaName typeName;

  public ClassType(Class<?> type) {
    this(JavaName.of(type));
  }

  public ClassType(String... names) {
    this(JavaName.of(names));
  }

  public ClassType(JavaName typeName, TypeArgument... typeArguments) {
    this.typeName = typeName;
    this.packageName = typeName.getPackageName();
    this.names = typeName.getSimpleNames().stream().map(ClassName::new).collect(toList());
    Collections.addAll(getTypeArguments(), typeArguments);
  }

  public ClassType addAnnotation(String... names) {
    return addAnnotations(new JavaAnnotation(JavaName.of(names)));
  }

  public ClassType addAnnotations(JavaAnnotation... annotations) {
    return addAnnotations(Arrays.asList(annotations));
  }

  public ClassType addAnnotations(int index, JavaAnnotation... annotations) {
    return addAnnotations(index, Arrays.asList(annotations));
  }

  public ClassType addAnnotations(int index, List<JavaAnnotation> annotations) {
    names.get(index).getAnnotations().addAll(annotations);
    return this;
  }

  public ClassType addAnnotations(List<JavaAnnotation> annotations) {
    return addAnnotations(names.size() - 1, annotations);
  }

  @Override
  public Listing apply(Listing listing) {
    applyPackageAndNames(listing);
    return listing;
  }

  public Listing applyPackageAndNames(Listing listing) {
    //    ClassType candidate = this;
    //    while (candidate != null) {
    //      if (listing.getImportDeclarations().testSingleTypeImport(candidate.typeName)) {
    //        int fromIndex = candidate.names.size() - 1;
    //        int toIndex = names.size();
    //        return listing.add(names.subList(fromIndex, toIndex), ".");
    //      }
    //      candidate = candidate.getEnclosingClassType();
    //    }
    //    if (listing.getImportDeclarations().testOnDemandImport(this.typeName)) {
    //      return listing.add(names, ".");
    //    }
    if (!getPackageName().isEmpty()) {
      listing.add(getPackageName()).add('.');
    }
    return listing.add(getNames(), ".");
  }

  @Override
  public List<JavaAnnotation> getAnnotations() {
    return names.get(names.size() - 1).getAnnotations();
  }

  @Override
  public ElementType getAnnotationTarget() {
    return ElementType.TYPE_USE;
  }

  public Optional<ClassType> getEnclosingClassType() {
    if (names.size() == 1) {
      return Optional.empty();
    }
    return Optional.of(new ClassType(typeName.getEnclosing().get()));
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

  public JavaName getTypeName() {
    return typeName;
  }

  @Override
  public boolean isJavaLangObject() {
    return getTypeName().isJavaLangObject();
  }
}
