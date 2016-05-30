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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.SecureClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.processing.Processor;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

/**
 * In-memory file manager and compiler support.
 *
 * @author Christian Stein
 */
interface Compilation {

  class ByteArrayFileObject extends SimpleJavaFileObject {

    private ByteArrayOutputStream stream;

    public ByteArrayFileObject(String canonical, Kind kind) {
      super(URI.create("listing:///" + canonical.replace('.', '/') + kind.extension), kind);
    }

    public byte[] getBytes() {
      return stream.toByteArray();
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
      this.stream = new ByteArrayOutputStream(2000);
      return stream;
    }
  }

  class CharContentFileObject extends SimpleJavaFileObject {

    private final String charContent;
    private final long lastModified;

    public CharContentFileObject(URI uri, String charContent) {
      super(uri, JavaFileObject.Kind.SOURCE);
      this.charContent = charContent;
      this.lastModified = System.currentTimeMillis();
    }

    @Override
    public String getCharContent(boolean ignoreEncodingErrors) {
      return charContent;
    }

    @Override
    public long getLastModified() {
      return lastModified;
    }
  }

  class Manager extends ForwardingJavaFileManager<StandardJavaFileManager> {

    private final Map<String, ByteArrayFileObject> map = new HashMap<>();
    private final ClassLoader parent;

    public Manager(StandardJavaFileManager standardManager, ClassLoader parent) {
      super(standardManager);
      this.parent = parent != null ? parent : getClass().getClassLoader();
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
      return new SecureLoader(parent, map);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location l, String name, Kind kind, FileObject s) {
      isSameFile(s, s);
      ByteArrayFileObject object = new ByteArrayFileObject(name, kind);
      map.put(name, object);
      return object;
    }

    @Override
    public boolean isSameFile(FileObject a, FileObject b) {
      return a.toUri().equals(b.toUri());
    }
  }

  static class SecureLoader extends SecureClassLoader {
    private final Map<String, ByteArrayFileObject> map;

    public SecureLoader(ClassLoader parent, Map<String, ByteArrayFileObject> map) {
      super(parent);
      this.map = map;
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
      ByteArrayFileObject object = map.get(className);
      if (object == null) {
        throw new ClassNotFoundException(className);
      }
      byte[] b = object.getBytes();
      return super.defineClass(className, b, 0, b.length);
    }
  }

  public static ClassLoader compile(
      ClassLoader parent, List<String> options, List<Processor> processors, JavaUnit... units) {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    Tool.check(compiler, "no system java compiler available - JDK is required!");
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
    StandardJavaFileManager sjfm =
        compiler.getStandardFileManager(diagnostics, Locale.getDefault(), StandardCharsets.UTF_8);
    Manager manager = new Manager(sjfm, parent);

    CompilationTask task =
        compiler.getTask(
            null,
            manager,
            diagnostics,
            options,
            null, // names of classes to be processed by annotation processing, null means none
            Arrays.stream(units).map(Compilation::source).collect(Collectors.toList()));
    if (!processors.isEmpty()) task.setProcessors(processors);
    boolean success = task.call();
    if (!success) throw new RuntimeException("compilation failed! " + diagnostics.getDiagnostics());
    return manager.getClassLoader(StandardLocation.CLASS_PATH);
  }

  public static JavaFileObject source(JavaUnit unit) {
    TypeDeclaration<?> declaration = unit.getEponymousDeclaration().get();
    URI uri = unit.getPackageDeclaration().toURI(declaration.getName() + ".java");
    return source(uri, unit.list());
  }

  public static JavaFileObject source(URI uri, String charContent) {
    return new CharContentFileObject(uri, charContent);
  }
}
