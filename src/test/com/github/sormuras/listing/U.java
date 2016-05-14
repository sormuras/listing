package com.github.sormuras.listing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_USE)
public @interface U {

  @U int NUMBER = 4711;

  String USE = "@" + U.class.getCanonicalName();
}
