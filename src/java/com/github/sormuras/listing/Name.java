package com.github.sormuras.listing;

/**
 * Names are used to refer to entities declared in a program.
 *
 * @see JLS: <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-6.html">Names</a>
 */
public final class Name {

  private final String[] identifiers;
  private final String qualified;

  public Name(String... identifiers) {
    this.identifiers = identifiers;
    this.qualified = String.join(".", getIdentifiers());
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    return hashCode() == other.hashCode();
  }

  public String[] getIdentifiers() {
    return identifiers;
  }

  public String getQualified() {
    return qualified;
  }

  @Override
  public int hashCode() {
    return qualified.hashCode();
  }

  @Override
  public String toString() {
    return "Name[" + getQualified() + "]";
  }

}
