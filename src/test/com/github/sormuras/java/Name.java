package com.github.sormuras.java;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.lang.model.SourceVersion;

/**
 * Names are used to refer to entities declared in a program.
 *
 * <p>A declared entity is a package, class type (normal or enum), interface type (normal or
 * annotation type), member (class, interface, field, or method) of a reference type, type parameter
 * (of a class, interface, method or constructor), parameter (to a method, constructor, or exception
 * handler), or local variable.
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-6.html
 */
public final class Name {

  /** Compiled <code>"."</code> pattern used to split canonical package and type names. */
  public static final Pattern DOT = Pattern.compile("\\.");

  /** Cast/convert any object to an instance of {@link Name}. */
  public static Name cast(Object any) {
    if (any == null) {
      return null;
    }
    if (any instanceof Name) {
      return (Name) any;
    }
    if (any instanceof Class) {
      return name((Class<?>) any);
    }
    if (any instanceof Enum) {
      return name((Enum<?>) any);
    }
    if (any instanceof Member) {
      return name((Member) any);
    }
    if (any instanceof String[]) {
      return name((String[]) any);
    }
    if (any instanceof List<?>) {
      return name(((List<?>) any).stream().map(i -> i.toString()).collect(Collectors.toList()));
    }
    throw new IllegalArgumentException("can't cast/convert instance of " + any.getClass());
  }

  /** Create name instance for the given class instance. */
  public static Name name(Class<?> type) {
    // java[.]lang[.]Thread$State
    String[] packageNames = DOT.split(type.getName());
    // java[.]lang[.]Thread[.]State
    String[] identifiers = DOT.split(type.getCanonicalName());
    return new Name(packageNames.length - 1, Arrays.asList(identifiers));
  }

  /** Create new Name based on the class type and declared member name. */
  public static Name name(Class<?> declaringType, String declaredMemberName) {
    Name declaringName = name(declaringType);
    List<String> names = new ArrayList<>(declaringName.size + 1);
    names.addAll(declaringName.identifiers);
    names.add(declaredMemberName);
    return new Name(declaringName.packageLevel, names);
  }

  /** Create name instance for the given enum constant. */
  public static Name name(Enum<?> constant) {
    return name(constant.getDeclaringClass(), constant.name());
  }

  /**
   * Create name instance for the identifiers.
   *
   * <p>The fully qualified class name {@code abc.xyz.Alphabet} can be created by:
   *
   * <pre>
   * name(2, "abc", "xyz", "Alphabet")
   * </pre>
   *
   * @throws AssertionError if any identifier is not a syntactically valid qualified name.
   */
  public static Name name(int packageLevel, List<String> names) {
    assert packageLevel >= 0 : "package level must not be < 0, but is " + packageLevel;
    assert packageLevel <= names.size() : "package level " + packageLevel + " too high: " + names;
    assert names.stream().allMatch(SourceVersion::isName) : "non-name in " + names;
    return new Name(packageLevel, names);
  }

  /**
   * Create name instance for the identifiers by delegating to {@link #name(int, List)}.
   *
   * <p>The package level is determined by the first capital name of the list.
   */
  public static Name name(List<String> names) {
    int size = names.size();
    IntPredicate uppercase = index -> Character.isUpperCase(names.get(index).codePointAt(0));
    int packageLevel = IntStream.range(0, size).filter(uppercase).findFirst().orElse(size);
    return name(packageLevel, names);
  }

  /** Create new Name based on the member instance. */
  public static Name name(Member member) {
    return name(member.getDeclaringClass(), member.getName());
  }

  /** Create name instance for the identifiers by delegating to {@link #name(List)}. */
  public static Name name(String... identifiers) {
    return name(Arrays.asList(identifiers));
  }

  /** Create new Name based on the class type and declared member name. */
  public static Name reflect(Class<?> type, String declaredMemberName) {
    try {
      Member field = type.getDeclaredField(declaredMemberName);
      return name(field);
    } catch (Exception expected) {
      // fall-through
    }
    for (Member method : type.getDeclaredMethods()) {
      if (method.getName().equals(declaredMemberName)) {
        return name(method);
      }
    }
    throw new AssertionError("Member '" + declaredMemberName + "' of " + type + " lookup failed!");
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

  public List<String> simpleNames() {
    return identifiers.subList(packageLevel, size);
  }

  public int size() {
    return size;
  }

  public String topLevelName() {
    return identifiers.get(packageLevel);
  }

  @Override
  public String toString() {
    return String.format("Name{%s/%s}", packageName, String.join(".", simpleNames()));
  }
}
