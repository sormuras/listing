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

import com.github.sormuras.listing.Compilation;
import com.github.sormuras.listing.Listing;
import com.github.sormuras.listing.Listing.Builder;
import com.github.sormuras.listing.Name;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import javax.tools.JavaFileObject;

/**
 * Java compilation unit.
 *
 * <pre>
 * CompilationUnit:
 *   [PackageDeclaration] {ImportDeclaration} {TypeDeclaration}
 * </pre>
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.3
 */
public class CompilationUnit implements DeclarationContainer {

  public static CompilationUnit of(String packageName) {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName(packageName);
    return unit;
  }

  private List<TypeDeclaration> declarations = new ArrayList<>();
  private ImportDeclarations importDeclarations = new ImportDeclarations();
  private PackageDeclaration packageDeclaration = new PackageDeclaration();

  @Override
  public Listing apply(Listing listing) {
    listing.add(getPackageDeclaration());
    listing.add(getImportDeclarations());
    getDeclarations().forEach(declaration -> declaration.apply(listing));
    return listing;
  }

  /** Compile and return {@link Class} instance. */
  public Class<?> compile() throws ClassNotFoundException {
    ClassLoader loader = Compilation.compile(toJavaFileObject());
    TypeDeclaration declaration = getEponymousDeclaration().get();
    return loader.loadClass(getPackageDeclaration().resolve(declaration.getName()));
  }

  /** Compile and create new instance. */
  @SuppressWarnings("unchecked")
  public <T> T compile(Class<T> clazz, Object... args) {
    try {
      return (T) compile().getDeclaredConstructors()[0].newInstance(args);
    } catch (Exception exception) {
      throw new AssertionError("compiling or instantiating failed", exception);
    }
  }

  /** Compile and create new instance. */
  public <T> T compile(Class<T> clazz, Supplier<Class<?>[]> typesProvider, Object... args) {
    try {
      Class<? extends T> subClass = compile().asSubclass(clazz);
      return subClass.getConstructor(typesProvider.get()).newInstance(args);
    } catch (Exception exception) {
      throw new AssertionError("compiling or instantiating failed", exception);
    }
  }

  @Override
  public <T extends TypeDeclaration> T declare(T declaration, String name) {
    DeclarationContainer.super.declare(declaration, name);
    declaration.setEnclosingDeclaration(null);
    declaration.setCompilationUnit(this);
    return declaration;
  }

  @Override
  public List<TypeDeclaration> getDeclarations() {
    return declarations;
  }

  /**
   * @return file name defining type declaration
   * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.6-510
   */
  public Optional<TypeDeclaration> getEponymousDeclaration() {
    List<TypeDeclaration> types = getDeclarations();
    // trivial case: no type present
    if (types.isEmpty()) {
      return Optional.empty();
    }
    // trivial case: only one type present
    TypeDeclaration declaration = types.get(0);
    // if multiple types are present, find first public one
    if (types.size() > 1) {
      declaration = types.stream().filter(TypeDeclaration::isPublic).findFirst().get();
    }
    return Optional.of(declaration);
  }

  public ImportDeclarations getImportDeclarations() {
    return importDeclarations;
  }

  public PackageDeclaration getPackageDeclaration() {
    return packageDeclaration;
  }

  public String getPackageName() {
    if (getPackageDeclaration().isUnnamed()) {
      return "";
    }
    return getPackageDeclaration().getName().getPackageName();
  }

  @Override
  public boolean isEmpty() {
    return getDeclarations().isEmpty()
        && getPackageDeclaration().isEmpty()
        && getImportDeclarations().isEmpty();
  }

  @Override
  public UnaryOperator<Builder> listOperator() {
    return builder -> builder.setImported(getImportDeclarations());
  }

  public void setPackageName(String packageName) {
    getPackageDeclaration().setName(Name.of(packageName));
  }

  public JavaFileObject toJavaFileObject() {
    TypeDeclaration declaration = getEponymousDeclaration().get();
    URI uri = getPackageDeclaration().toUri(declaration.getName() + ".java");
    return Compilation.source(uri, list());
  }
}
