package com.github.sormuras.java;

import java.util.Arrays;

public class Name {

  public static Name name(Class<?> type) {
    String packageName = type.getPackage().getName();
    String canonical = type.getCanonicalName();
    return new Name(packageName.split("\\.").length, canonical.split("\\."));
  }

  public static Name name(String... identifiers) {
    int index = identifiers.length;
    for (int i = 0; i < identifiers.length; i++) {
      if (Character.isUpperCase(identifiers[i].charAt(0))) {
        index = i;
        break;
      }
    }
    return new Name(index, identifiers);
  }

  private final String canonical;

  private final String[] identifiers;
  private final int packageIndex;
  private final String packageName;

  public Name(int packageIndex, String... identifiers) {
    this.packageIndex = packageIndex;
    this.identifiers = identifiers;
    this.canonical = String.join(".", identifiers);
    this.packageName = String.join(".", Arrays.asList(identifiers).subList(0, packageIndex));
  }

  public String canonical() {
    return canonical;
  }

  public Name enclosing() {
    if (identifiers.length <= 1) {
      throw new IllegalArgumentException();
    }
    String[] identifiers = new String[this.identifiers.length - 1];
    System.arraycopy(this.identifiers, 0, identifiers, 0, identifiers.length);
    return new Name(Math.min(packageIndex, identifiers.length), identifiers);
  }

  public String[] identifiers() {
    return identifiers;
  }

  public String packageName() {
    return packageName;
  }

  @Override
  public String toString() {
    return "Name {" + canonical + "}";
  }
}
