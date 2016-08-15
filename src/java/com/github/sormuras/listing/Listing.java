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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Listing {

  public static class Builder {
    private static boolean not(Name name) {
      return false;
    }

    public Predicate<Name> imported = Builder::not;
    public String indentationString = "  ";
    public String lineSeparator = "\n";
    public boolean omitJavaLangPackage = false;

    public Listing build() {
      return new Listing(this);
    }

    public Builder setImported(Predicate<Name> imported) {
      this.imported = imported;
      return this;
    }

    public Builder setIndentationString(String indentationString) {
      this.indentationString = indentationString;
      return this;
    }

    public Builder setLineSeparator(String lineSeparator) {
      this.lineSeparator = lineSeparator;
      return this;
    }

    public Builder setOmitJavaLangPackage(boolean omitJavaLangPackage) {
      this.omitJavaLangPackage = omitJavaLangPackage;
      return this;
    }
  }

  public static final Pattern METHODCHAIN_PATTERN = Pattern.compile("\\{|\\.|\\}");
  public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{.+?\\}");

  public static Builder builder() {
    return new Builder();
  }

  private final Deque<String> collectedLines = new ArrayDeque<>(512);
  private final StringBuilder currentLine = new StringBuilder(512);
  private final Predicate<Name> imported;
  private int indentationDepth = 0;
  private final String indentationString;
  private final String lineSeparator;
  private final Deque<String> nameStack = new ArrayDeque<>(8);
  private final boolean omitJavaLangPackage;

  public Listing() {
    this(builder());
  }

  public Listing(Builder builder) {
    this.lineSeparator = builder.lineSeparator;
    this.indentationString = builder.indentationString;
    this.omitJavaLangPackage = builder.omitJavaLangPackage;
    this.imported = builder.imported;
  }

  public Listing add(char character) {
    currentLine.append(character);
    return this;
  }

  public Listing add(CharSequence text) {
    currentLine.append(text);
    return this;
  }

  /** Add list of listables using newline separator. */
  public Listing add(List<? extends Listable> listables) {
    return add(listables, Listable.NEWLINE);
  }

  /** Add list of listables using given listable separator. */
  public Listing add(List<? extends Listable> listables, Listable separator) {
    if (listables.isEmpty()) {
      return this;
    }
    if (listables.size() == 1) {
      add(listables.get(0));
      return this;
    }
    Spliterator<? extends Listable> spliterator = listables.spliterator();
    spliterator.tryAdvance(this::add);
    spliterator.forEachRemaining(listable -> separator.apply(this).add(listable));
    return this;
  }

  /**
   * Add list of listables using given textual separator inline.
   *
   * <p>For example: {@code "a, b, c"}, {@code "a & b & c"} or {@code "[][][]"}
   */
  public Listing add(List<? extends Listable> listables, CharSequence separator) {
    return add(listables, listing -> listing.add(separator));
  }

  /** Applies the passed listable instance to this listing. */
  public Listing add(Listable listable) {
    if (listable == null) {
      return this;
    }
    // prevent stack overflow by delegating to specialized method
    if (listable instanceof Name) {
      return add((Name) listable);
    }
    return listable.apply(this);
  }

  /** Add name respecting name map. */
  public Listing add(Name name) {
    // never call `name.apply(this)` here - looping alert!
    if (imported.test(name)) {
      return add(name.getLastSimpleName());
    }
    // "java.lang" member
    if (omitJavaLangPackage && name.isJavaLangPackage()) {
      return add(String.join(".", name.getSimpleNames()));
    }
    return add(name.getCanonicalName());
  }

  /**
   * Parse source string and replace placeholder with {@link #add()}-calls to this {@link Listing}
   * instance.
   */
  public Listing add(String source, Object... args) {
    Matcher matcher = PLACEHOLDER_PATTERN.matcher(source);

    int argumentIndex = 0;
    int sourceIndex = 0;
    while (matcher.find()) {
      if (sourceIndex < matcher.start()) {
        add(source.substring(sourceIndex, matcher.start()));
      }
      sourceIndex = matcher.end();
      // handle simple placeholder
      String placeholder = matcher.group(0);
      if (placeholder.equals("{S}")) {
        add(Tool.escape(args[argumentIndex++].toString()));
        continue;
      }
      if (placeholder.equals("{N}")) {
        add(Name.cast(args[argumentIndex++]));
        continue;
      }
      if (placeholder.equals("{L}")) {
        add((Listable) args[argumentIndex++]);
        continue;
      }
      // convert unknown placeholder to chained method call sequence
      Object argument = args[argumentIndex++];
      try (Scanner scanner = new Scanner(placeholder)) {
        scanner.useDelimiter(METHODCHAIN_PATTERN);
        Object result = argument;
        while (scanner.hasNext()) {
          result = result.getClass().getMethod(scanner.next()).invoke(result);
        }
        if (result instanceof Optional) {
          result = ((Optional<?>) result).get();
        }
        if (result instanceof Name) {
          add((Name) result);
          continue;
        }
        if (result instanceof Listable) {
          add((Listable) result);
          continue;
        }
        add(String.valueOf(result));
      } catch (Exception exception) {
        throw new IllegalArgumentException(
            "error parsing: '" + placeholder + "' source='" + source + "'", exception);
      }
    }
    add(source.substring(sourceIndex));
    return this;
  }

  public Listing fmt(Locale locale, String format, Object... args) {
    return add(args.length == 0 ? format : String.format(locale, format, args));
  }

  public Listing fmt(String format, Object... args) {
    return add(args.length == 0 ? format : String.format(format, args));
  }

  public Deque<String> getCollectedLines() {
    return collectedLines;
  }

  public StringBuilder getCurrentLine() {
    return currentLine;
  }

  public int getCurrentLineNumber() {
    return collectedLines.size() + 1;
  }

  public Predicate<Name> getImported() {
    return imported;
  }

  public int getIndentationDepth() {
    return indentationDepth;
  }

  public String getIndentationString() {
    return indentationString;
  }

  public String getLineSeparator() {
    return lineSeparator;
  }

  public Deque<String> getNameStack() {
    return nameStack;
  }

  public Listing indent(int times) {
    indentationDepth += times;
    if (indentationDepth < 0) {
      indentationDepth = 0;
    }
    return this;
  }

  public boolean isLastLineEmpty() {
    if (collectedLines.isEmpty()) {
      return true;
    }
    return collectedLines.getLast().isEmpty();
  }

  public boolean isOmitJavaLangPackage() {
    return omitJavaLangPackage;
  }

  /** Carriage return and line feed. */
  public Listing newline() {
    String newline = currentLine.toString(); // Tool.trimRight(currentLine.toString());
    currentLine.setLength(0);
    // trivial case: empty line (only add it if last line is not empty)
    if (newline.isEmpty()) {
      if (!isLastLineEmpty()) {
        collectedLines.add("");
      }
      return this;
    }
    // trivial case: no indentation, just add the line
    if (indentationDepth == 0) {
      collectedLines.add(newline);
      return this;
    }
    // "insert" indentation pattern in front of the new line
    int capacity = indentationDepth * indentationString.length() + newline.length();
    StringBuilder indentedLine = new StringBuilder(capacity);
    IntStream.range(0, indentationDepth).forEach(i -> indentedLine.append(indentationString));
    indentedLine.append(newline);
    collectedLines.add(indentedLine.toString());
    return this;
  }

  public Listing pop() {
    nameStack.pop();
    return this;
  }

  public Listing push(String name) {
    if (name == null) {
      name = "<null>";
    }
    nameStack.push(name);
    return this;
  }

  @Override
  public String toString() {
    if (collectedLines.isEmpty()) {
      return currentLine.toString();
    }
    return String.join(lineSeparator, collectedLines) + lineSeparator + currentLine.toString();
  }

  /** Removes empty lines from the end of the collected lines. */
  public Listing trim() {
    while (currentLine.length() > 0 && currentLine.lastIndexOf(" ") == currentLine.length() - 1) {
      currentLine.setLength(currentLine.length() - 1);
    }
    while (isLastLineEmpty()) {
      if (collectedLines.isEmpty()) {
        return this;
      }
      collectedLines.removeLast();
    }
    return this;
  }
}
