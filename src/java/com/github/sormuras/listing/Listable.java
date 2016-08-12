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

import java.io.Serializable;
import java.util.function.UnaryOperator;

@FunctionalInterface
public interface Listable extends UnaryOperator<Listing>, Comparable<Listable>, Serializable {

  class Identity implements Listable {

    @Override
    public Listing apply(Listing listing) {
      return listing;
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public String list() {
      return "";
    }

    @Override
    public String toString() {
      return "Listing.IDENTITY";
    }
  }

  Listable IDENTITY = new Identity();

  Listable NEWLINE = Listing::newline;

  Listable SPACE = listing -> listing.add(' ');

  @Override
  default int compareTo(Listable other) {
    return comparisonKey().compareTo(other.comparisonKey());
  }

  default String comparisonKey() {
    return getClass().getSimpleName().toLowerCase() + "#" + toString().toLowerCase();
  }

  default boolean isEmpty() {
    return list().isEmpty();
  }

  default String list() {
    return list(UnaryOperator.identity());
  }

  default String list(UnaryOperator<Listing.Builder> customOperator) {
    Listing.Builder builder = Listing.builder();
    Listing listing = listOperator().andThen(customOperator).apply(builder).build();
    return listing.add(this).toString();
  }

  default UnaryOperator<Listing.Builder> listOperator() {
    return UnaryOperator.identity();
  }
}
