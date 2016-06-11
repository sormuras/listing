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
package de.codeturm.listing;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

public class Lines {

  private final List<String> collectedLines = new ArrayList<>(512);
  private final StringBuilder currentLine = new StringBuilder(512);
  private int indentationDepth = 0;
  private final String indentationString;
  private final String lineSeparator;

  public Lines() {
    this("\n", "  ");
  }

  public Lines(String lineSeparator, String indentationString) {
    this.lineSeparator = lineSeparator;
    this.indentationString = indentationString;
  }

  public Lines add(char c) {
    currentLine.append(c);
    return this;
  }

  public Lines add(Locale locale, String format, Object... args) {
    return add(args.length == 0 ? format : String.format(locale, format, args));
  }

  public Lines add(String text) {
    currentLine.append(text);
    return this;
  }

  public Lines add(String format, Object... args) {
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

  public Lines indent(int times) {
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

  public Lines newline() {
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

  @Override
  public String toString() {
    if (collectedLines.isEmpty()) {
      return currentLine.toString();
    }
    return String.join(lineSeparator, collectedLines) + lineSeparator + currentLine.toString();
  }
}
