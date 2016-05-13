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

import static java.util.Collections.emptyList;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.processing.Processor;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

/**
 * JavaFile models compilation unit.
 *
 * <pre>
 * CompilationUnit:
 *   [PackageDeclaration] {ImportDeclaration} {TypeDeclaration}
 * </pre>
 *
 * @author Christian Stein
 * @see http://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.3
 */
public class JavaUnit implements Container {

  private final PackageDeclaration packageDeclaration;
  private final ImportDeclarations importDeclarations;
  private final List<TypeDeclaration<?>> types;

  public JavaUnit(PackageDeclaration packageDeclaration) {
    this.packageDeclaration = packageDeclaration;
    this.importDeclarations = new ImportDeclarations();
    this.types = new ArrayList<>();
  }

  public JavaUnit(String packageName) {
    this(new PackageDeclaration(packageName));
  }

  @Override
  public <D extends TypeDeclaration<D>> D addDeclaredType(D declaration) {
    declaration.setUnit(this);
    declaration.setEnclosingType(null);
    getDeclaredTypes().add(declaration);
    return declaration;
  }

  @Override
  public Listing apply(Listing listing) {
    listing.add(packageDeclaration);
    if (!listing.isLastLineEmpty() && !importDeclarations.isEmpty()) {
      listing.newline();
    }
    listing.add(importDeclarations);
    if (!listing.isLastLineEmpty() && !getDeclaredTypes().isEmpty()) {
      listing.newline();
    }
    getDeclaredTypes().forEach(listing::add);
    return listing;
  }

  public Class<?> compile() {
    ClassLoader loader = compile(getClass().getClassLoader(), emptyList(), emptyList());
    try {
      TypeDeclaration<?> declaration = getEponymousDeclaration().get();
      return loader.loadClass(getPackageDeclaration().resolve(declaration.getName()));
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException("loading class failed after successful compilation?!", e);
    }
  }

  @SuppressWarnings("unchecked")
  public <T> T compile(Class<T> clazz, Object... args) {
    try {
      return (T) compile().getDeclaredConstructors()[0].newInstance(args);
    } catch (Throwable t) {
      throw new IllegalStateException("compiling or instantiating failed", t);
    }
  }

  public <T> T compile(Class<T> clazz, Supplier<Class<?>[]> typesProvider, Object... args) {
    try {
      Class<? extends T> subClass = compile().asSubclass(clazz);
      return subClass.getConstructor(typesProvider.get()).newInstance(args);
    } catch (Throwable t) {
      throw new IllegalStateException("compiling or instantiating failed", t);
    }
  }

  public ClassLoader compile(ClassLoader parent, List<String> options, List<Processor> processors) {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    Tool.assume(compiler != null, "no system java compiler available - JDK is required!");
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
    StandardJavaFileManager sjfm =
        compiler.getStandardFileManager(diagnostics, Locale.getDefault(), StandardCharsets.UTF_8);
    Manager manager = new Manager(sjfm, parent);
    TypeDeclaration<?> declaration = getEponymousDeclaration().get();
    URI uri = getPackageDeclaration().toURI(declaration.getName() + ".java");
    CompilationTask task =
        compiler.getTask(
            null,
            manager,
            diagnostics,
            options,
            null, // names of classes to be processed by annotation processing, null means no classes
            Collections.singleton(manager.getJavaFileForInput(uri, list())));
    if (!processors.isEmpty()) task.setProcessors(processors);
    boolean success = task.call();
    if (!success) throw new RuntimeException("compilation failed! " + diagnostics.getDiagnostics());
    return manager.getClassLoader(StandardLocation.CLASS_PATH);
  }

  @Override
  public List<TypeDeclaration<?>> getDeclaredTypes() {
    return types;
  }

  /**
   * @return file name defining type declaration
   * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.6-510
   */
  public Optional<TypeDeclaration<?>> getEponymousDeclaration() {
    List<TypeDeclaration<?>> types = getDeclaredTypes();
    // trivial case: no type present
    if (types.isEmpty()) return Optional.empty();
    // trivial case: only one type present
    TypeDeclaration<?> declaration = types.get(0);
    // if multiple types are present, look find first public one
    if (types.size() > 1) {
      declaration = types.stream().filter(t -> t.isPublic()).findFirst().get();
    }
    return Optional.of(declaration);
  }

  public ImportDeclarations getImportDeclarations() {
    return importDeclarations;
  }

  public PackageDeclaration getPackageDeclaration() {
    return packageDeclaration;
  }
}
