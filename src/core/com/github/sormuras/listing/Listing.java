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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Spliterator;
import java.util.stream.IntStream;

public class Listing {

  private final List<String> collectedLines = new ArrayList<>();
  private final StringBuilder currentLine = new StringBuilder();
  private int indentationDepth = 0;
  private final String indentationString;
  private final String lineSeparator;

  public Listing() {
    this("\n", "  ");
  }

  public Listing(String lineSeparator, String indentationString) {
    this.lineSeparator = lineSeparator;
    this.indentationString = indentationString;
  }

  public Listing add(char c) {
    currentLine.append(c);
    return this;
  }

  public Listing add(JavaName name) {
    // never call `name.apply(this)` here - looping alert!
    return add(name.getCanonicalName());
  }

  /** newline separator */
  public Listing add(List<? extends Listable> listables) {
    return add(listables, Listable.NEWLINE);
  }

  /** inline text separator: "{@code a, b, c}" or "{@code a & b & c}" or "{@code [][][]}" */
  public Listing add(List<? extends Listable> listables, String separator) {
    return add(listables, listing -> listing.add(separator));
  }

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

  public Listing add(Listable listable) {
    // prevent stack overflow by delegating to specialized method
    if (listable instanceof JavaName) {
      return add((JavaName) listable);
    }
    return listable.apply(this);
  }

  public Listing add(Locale locale, String format, Object... args) {
    return add(args.length == 0 ? format : String.format(locale, format, args));
  }

  public Listing add(String text) {
    currentLine.append(text);
    return this;
  }

  public Listing add(String format, Object... args) {
    return add(args.length == 0 ? format : String.format(format, args));
  }

  public List<String> getCollectedLines() {
    return collectedLines;
  }

  public StringBuilder getCurrentLine() {
    return currentLine;
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
    return collectedLines.get(collectedLines.size() - 1).isEmpty();
  }

  public Listing newline() {
    String newline = Tool.trimRight(currentLine.toString());
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

  @Override
  public String toString() {
    if (collectedLines.isEmpty()) {
      return currentLine.toString();
    }
    return String.join(lineSeparator, collectedLines) + lineSeparator + currentLine.toString();
  }
}
