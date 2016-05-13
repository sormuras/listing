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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;

/**
 * In-memory file manager.
 *
 * @author Christian Stein
 */
public class Manager extends ForwardingJavaFileManager<StandardJavaFileManager> {

  static class ByteArrayFileObject extends SimpleJavaFileObject {
    ByteArrayOutputStream stream;

    ByteArrayFileObject(String canonical, Kind kind) {
      super(URI.create("javaunit:///" + canonical.replace('.', '/') + kind.extension), kind);
    }

    byte[] getBytes() {
      return stream.toByteArray();
    }

    @Override
    public CharSequence getCharContent(boolean ignoreErrors) throws IOException {
      return new String(getBytes(), StandardCharsets.UTF_8.name());
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
      this.stream = new ByteArrayOutputStream(2000);
      return stream;
    }
  }

  static class CharContentFileObject extends SimpleJavaFileObject {

    private final String charContent;
    private final long lastModified;

    CharContentFileObject(URI uri, String charContent) {
      super(uri, JavaFileObject.Kind.SOURCE);
      this.charContent = charContent;
      this.lastModified = System.currentTimeMillis();
    }

    @Override
    public String getCharContent(boolean ignoreEncodingErrors) {
      return charContent;
    }

    @Override
    public InputStream openInputStream() throws IOException {
      return new ByteArrayInputStream(getCharContent(true).getBytes());
    }

    @Override
    public long getLastModified() {
      return lastModified;
    }
  }

  class SecureLoader extends SecureClassLoader {
    SecureLoader(ClassLoader parent) {
      super(parent);
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

  final Map<String, ByteArrayFileObject> map = new HashMap<>();
  final ClassLoader parent;

  Manager(StandardJavaFileManager standardManager, ClassLoader parent) {
    super(standardManager);
    this.parent = parent != null ? parent : getClass().getClassLoader();
  }

  @Override
  public ClassLoader getClassLoader(Location location) {
    return new SecureLoader(parent);
  }

  public JavaFileObject getJavaFileForInput(URI uri, String charContent) {
    return new CharContentFileObject(uri, charContent);
  }

  @Override
  public JavaFileObject getJavaFileForOutput(
      Location location, String className, Kind kind, FileObject sibling) throws IOException {
    ByteArrayFileObject object = new ByteArrayFileObject(className, kind);
    map.put(className, object);
    return object;
  }

  @Override
  public boolean isSameFile(FileObject a, FileObject b) {
    return a.toUri().equals(b.toUri());
  }
}
