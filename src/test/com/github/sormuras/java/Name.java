package com.github.sormuras.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import javax.lang.model.SourceVersion;

public final class Name {

  public static final Pattern DOT = Pattern.compile("\\.");

  public static Name name(Class<?> type) {
    String[] packageNames = DOT.split(type.getName()); // java[.]lang[.]Thread$State
    String[] identifiers = DOT.split(type.getCanonicalName()); // java[.]lang[.]Thread[.]State
    return new Name(packageNames.length - 1, Arrays.asList(identifiers));
  }

  public static Name name(Enum<?> constant) {
    Name declaringName = name(constant.getDeclaringClass());
    List<String> names = new ArrayList<>(declaringName.size + 1);
    names.addAll(declaringName.identifiers);
    names.add(constant.name());
    return new Name(declaringName.packageLevel, names);
  }

  public static Name name(List<String> names) {
    assert names.stream().allMatch(SourceVersion::isName) : "non-name in " + names;
    int size = names.size();
    IntPredicate uppercase = index -> Character.isUpperCase(names.get(index).codePointAt(0));
    int level = IntStream.range(0, size).filter(uppercase).findFirst().orElse(size);
    return new Name(level, names);
  }

  public static Name name(String... identifiers) {
    return name(Arrays.asList(identifiers));
  }

  private final String canonical;
  private final List<String> identifiers;
  private final int packageLevel;
  private final String packageName;
  private final int size;

  private Name(int packageLevel, List<String> identifiers) {
    this.packageLevel = packageLevel;
    this.identifiers = Collections.unmodifiableList(identifiers);
    this.canonical = String.join(".", identifiers);
    this.packageName = String.join(".", identifiers.subList(0, packageLevel));
    this.size = identifiers.size();
  }

  public String canonical() {
    return canonical;
  }

  public Name enclosing() {
    if (!isEnclosed()) {
      throw new IllegalStateException(String.format("not enclosed: '%s'", this));
    }
    int shrinkedByOne = size - 1;
    int newPackageLevel = Math.min(packageLevel, shrinkedByOne);
    return new Name(newPackageLevel, identifiers.subList(0, shrinkedByOne));
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

  @Override
  public int hashCode() {
    return canonical.hashCode();
  }

  public List<String> identifiers() {
    return identifiers;
  }

  public boolean isEnclosed() {
    return size > 1;
  }

  public boolean isJavaLangObject() {
    return size == 3 && "java.lang.Object".equals(canonical);
  }

  public boolean isJavaLangPackage() {
    return packageLevel == 2 && "java.lang".equals(packageName);
  }

  public String lastName() {
    return identifiers.get(size - 1);
  }

  public String packageName() {
    return packageName;
  }

  public Iterable<String> simpleNames() {
    return identifiers.subList(packageLevel, size);
  }

  public int size() {
    return size;
  }

  @Override
  public String toString() {
    return "Name {" + canonical + "}";
  }
}
