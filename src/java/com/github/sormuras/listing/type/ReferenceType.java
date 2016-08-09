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

package com.github.sormuras.listing.type;

import com.github.sormuras.listing.Annotated;

/**
 * There are four kinds of reference types: class types (§8.1), interface types (§9.1), type
 * variables (§4.4), and array types (§10.1).
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.3
 */
public abstract class ReferenceType extends Annotated implements JavaType {}
