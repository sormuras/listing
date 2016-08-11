package com.github.sormuras.listing.unit;

import static javax.lang.model.element.Modifier.STATIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sormuras.listing.Listing;
import com.github.sormuras.listing.Name;
import com.github.sormuras.listing.Tests;
import java.lang.annotation.ElementType;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Test;

public class ImportDeclarationsTest {

  private static void check(Consumer<ImportDeclarations> consumer, String... expected) {
    ImportDeclarations declarations = new ImportDeclarations();
    assertTrue(declarations.isEmpty());
    consumer.accept(declarations);
    Listing listing = new Listing();
    listing.add(declarations);
    assertEquals(Arrays.asList(expected), new ArrayList<>(listing.getCollectedLines()));
    if (expected.length > 0) {
      assertFalse(declarations.isEmpty());
    }
  }

  private static void empty(ImportDeclarations declarations) {}

  @Test
  void emptyIsEmpty() {
    check(ImportDeclarationsTest::empty);
    assertTrue(new ImportDeclarations().isEmpty());
  }

  @Test
  void imports() throws Exception {
    ImportDeclarations imports = new ImportDeclarations();
    imports.addSingleStaticImport(Name.of(STATIC));
    imports.addSingleStaticImport(Name.of("org.junit", "Assert", "assertEquals"));
    imports.addSingleStaticImport(Name.of("org.junit", "Assert", "assertFalse"));
    imports.addSingleStaticImport(Name.of("org.junit", "Assert", "assertTrue"));
    imports.addStaticImportOnDemand(Name.of(Objects.class));
    imports.addSingleTypeImport(Name.of(ElementType.class));
    imports.addSingleTypeImport(Name.of(Member.class));
    imports.addTypeImportOnDemand(Name.of("java.util"));
    Tests.assertEquals(getClass(), "imports", imports);
    assertTrue(imports.test(Name.of(STATIC)));
    assertTrue(imports.test(Name.of(Objects.class, "requireNonNull")));
    assertTrue(imports.test(Name.of(Set.class)));
    assertTrue(imports.test(Name.of(Member.class)));
    assertTrue(imports.test(Name.of("org.junit", "Assert", "assertTrue")));
    assertFalse(imports.test(Name.of("org")));
    assertFalse(imports.test(Name.of(Test.class)));
    Listing listing = Listing.builder().setImported(imports).build();
    assertSame(imports, listing.getImported());
    listing.add(Name.of(STATIC)).newline();
    listing.add(Name.of(Objects.class, "requireNonNull")).newline();
    listing.add(Name.of(Test.class)).newline();
    assertEquals("STATIC\nrequireNonNull\norg.junit.jupiter.api.Test\n", listing.toString());
  }

  @Test
  void singleStaticImport() throws Exception {
    Member micros = TimeUnit.class.getField("MICROSECONDS");
    Name parameter = Name.of("java.lang.annotation", "ElementType", "PARAMETER");

    parameter.setTarget(ElementType.FIELD);
    parameter.setModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
    check(
        imports ->
            imports
                .addSingleStaticImport(Thread.State.NEW)
                .addSingleStaticImport(micros)
                .addSingleStaticImport(parameter),
        "import static java.lang.Thread.State.NEW;",
        "import static java.lang.annotation.ElementType.PARAMETER;",
        "import static java.util.concurrent.TimeUnit.MICROSECONDS;");
  }

  @Test
  void singleTypeImport() {
    check(i -> i.addSingleTypeImport(Set.class), "import java.util.Set;");
    check(
        imports ->
            imports
                .addSingleTypeImport(Set.class)
                .addSingleTypeImport(Map.Entry.class)
                .addSingleTypeImport(Name.of("java.io", "File")),
        "import java.io.File;",
        "import java.util.Map.Entry;",
        "import java.util.Set;");
  }

  @Test
  void staticImportOnDemand() {
    check(
        imports -> imports.addStaticImportOnDemand(Name.of("java.lang", "Thread", "State")),
        "import static java.lang.Thread.State.*;");
  }

  @Test
  void typeImportOnDemand() {
    check(imports -> imports.addTypeImportOnDemand(Name.of("java.util")), "import java.util.*;");
  }
}
