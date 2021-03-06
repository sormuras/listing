package com.github.sormuras.listing.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.expectThrows;

import com.github.sormuras.listing.Annotation;
import com.github.sormuras.listing.Listable;
import com.github.sormuras.listing.Listing;
import com.github.sormuras.listing.Name;
import com.github.sormuras.listing.Tests;
import java.lang.annotation.ElementType;
import java.net.URI;
import org.junit.jupiter.api.Test;

class PackageDeclarationTest {

  @Test
  void empty() {
    assertTrue(new PackageDeclaration().isEmpty());
  }

  @Test
  void list() {
    // unnamed
    assertEquals("", new PackageDeclaration().list());
    // simple
    assertEquals("package abc;\n", PackageDeclaration.of("abc").list());
    assertEquals("package abc.xyz;\n", PackageDeclaration.of("abc.xyz").list());
    // with annotation(s)
    PackageDeclaration annotated = PackageDeclaration.of("abc.xyz");
    annotated.addAnnotation(new Annotation(Name.of("abc", "PackageAnnotation")));
    Tests.assertEquals(getClass(), "annotated", annotated);
    // with (hand-crafted) Javadoc
    Listing listing = new Listing();
    listing.add("/**").newline();
    listing.add(" * Testing Javadoc on PackageDeclaration element.").newline();
    listing.add(" *").newline();
    listing.add(" * @since 1.0").newline();
    listing.add(" */").newline();
    listing.add(annotated);
    Tests.assertEquals(getClass(), "javadoc", listing);
  }

  @Test
  void separator() {
    assertEquals(Listable.NEWLINE, new PackageDeclaration().getAnnotationSeparator());
  }

  @Test
  void target() {
    assertEquals(ElementType.PACKAGE, PackageDeclaration.of("t").getAnnotationTarget());
  }

  @Test
  void unnamedAsStringFails() {
    Error error = expectThrows(AssertionError.class, () -> PackageDeclaration.of(""));
    assertTrue(error.getMessage().contains("empty"));
  }

  @Test
  void resolve() {
    assertEquals("Tag", new PackageDeclaration().resolve("Tag"));
    assertEquals("abc.xyz.Tag", PackageDeclaration.of("abc.xyz").resolve("Tag"));
  }

  @Test
  void uri() {
    assertEquals(URI.create("Tag"), new PackageDeclaration().toUri("Tag"));
    assertEquals(URI.create("abc/xyz/Tag"), PackageDeclaration.of("abc.xyz").toUri("Tag"));
  }
}
