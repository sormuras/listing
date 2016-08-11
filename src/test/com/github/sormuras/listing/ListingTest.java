package com.github.sormuras.listing;

import static com.github.sormuras.listing.Listable.IDENTITY;
import static com.github.sormuras.listing.Listable.NEWLINE;
import static com.github.sormuras.listing.Listable.SPACE;
import static java.lang.Math.PI;
import static java.util.Arrays.asList;
import static java.util.Locale.GERMAN;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.junit.jupiter.api.Test;

class ListingTest {

  @Test
  void addChar() {
    assertEquals("\0", new Listing().add('\0').toString());
    assertEquals("'0'", new Listing().add(Annotation.value('0')).toString());
    assertEquals("'\\u0000'", new Listing().add(Annotation.value('\0')).toString());
  }

  @Test
  void addFormattedString() {
    assertEquals("abc", new Listing().add("a%sc", "b").toString());
    assertEquals("abc", new Listing().add("abc", new Object[0]).toString());
    assertEquals("3,14159", new Listing().add(GERMAN, "%.5f", PI).toString());
    assertEquals("3,14159", new Listing().add(GERMAN, "3,14159").toString());
  }

  @Test
  void addListable() {
    assertEquals("", new Listing().add(IDENTITY).toString());
    assertEquals("α\n", new Listing().add('α').add(NEWLINE).toString());
    assertEquals(" ", new Listing().add(SPACE).toString());
  }

  @Test
  void addListOfListables() {
    List<Listable> list = new ArrayList<>();
    assertEquals("", new Listing().add(list).toString());
    list.add(Annotation.value('a'));
    assertEquals("'a'", new Listing().add(list).toString());
    list.add(Annotation.value('z'));
    assertEquals("'a'\n'z'", new Listing().add(list).toString());
    assertEquals("'a'-'z'", new Listing().add(list, "-").toString());
  }

  @Test
  void addName() {
    Listing listing = Listing.builder().setOmitJavaLangPackage(false).build();
    listing.add(Name.of(Object.class)).add('-');
    listing.add(Name.of(Objects.class)).add('-');
    listing.add(Name.of(Thread.State.class));
    assertEquals("java.lang.Object-java.util.Objects-java.lang.Thread.State", listing.toString());
    listing = Listing.builder().setOmitJavaLangPackage(true).build();
    listing.add(Name.of(Object.class)).add('-');
    listing.add(Name.of(Objects.class)).add('-');
    listing.add(Name.of(Thread.State.class));
    assertEquals("Object-java.util.Objects-Thread.State", listing.toString());
  }

  @Test
  void beginAndEnd() {
    Listing listing = new Listing();
    listing.add("BEGIN").newline();
    listing.add("END.").newline();
    assertEquals("BEGIN\nEND.\n", listing.toString());
    assertEquals(asList("BEGIN", "END."), new ArrayList<>(listing.getCollectedLines()));
  }

  @Test
  void defaults() {
    Listing listing = new Listing();
    assertEquals(0, listing.getIndentationDepth());
    assertEquals("  ", listing.getIndentationString());
    assertEquals("\n", listing.getLineSeparator());
    assertEquals(false, listing.getImported().test(Name.of(Test.class)));
    assertEquals(false, listing.isOmitJavaLangPackage());
    assertEquals("", listing.getCurrentLine().toString());
    assertEquals(1, listing.getCurrentLineNumber());
    assertEquals("", listing.trim().toString());
  }

  @Test
  void builder() {
    Listing.Builder builder = Listing.builder();
    builder.setImported(name -> !name.isEmpty());
    builder.setIndentationString(" ");
    builder.setLineSeparator("\r\n");
    builder.setOmitJavaLangPackage(true);
    Listing listing = builder.build();
    assertEquals(true, listing.getImported().test(Name.of(Test.class)));
    assertEquals(0, listing.getIndentationDepth());
    assertEquals(" ", listing.getIndentationString());
    assertEquals("\r\n", listing.getLineSeparator());
    assertEquals(true, listing.isOmitJavaLangPackage());
    assertEquals("", listing.getCurrentLine().toString());
    assertEquals(1, listing.getCurrentLineNumber());
    assertEquals("", listing.trim().toString());
  }

  @Test
  void indent() {
    Listing listing = Listing.builder().setIndentationString("\t").build();
    listing.add("BEGIN").newline();
    listing.indent(1).add("writeln('Hello, world.')").newline().indent(-1);
    listing.add("END.").newline();
    assertEquals(0, listing.getIndentationDepth());
    assertEquals("BEGIN\n\twriteln('Hello, world.')\nEND.\n", listing.toString());
    listing.indent(-100);
    assertEquals(0, listing.getIndentationDepth());
  }

  @Test
  void nameStack() {
    assertEquals(true, new Listing().getNameStack().isEmpty());
  }

  @Test
  void newlineProducesOnlyOneSingleBlankLine() {
    Listing listing = new Listing();
    assertEquals(0, listing.getCurrentLine().length());
    listing.newline().newline().newline().newline().newline();
    assertEquals(0, listing.getCurrentLine().length());
    listing.add("BEGIN").newline().newline().newline().newline().newline();
    assertEquals(0, listing.getCurrentLine().length());
    listing.add("END.").newline().newline().newline().newline().newline();
    assertEquals(0, listing.getCurrentLine().length());
    assertEquals("BEGIN\n\nEND.\n\n", listing.toString());
    assertEquals(0, listing.getCurrentLine().length());
    assertEquals(asList("BEGIN", "", "END.", ""), new ArrayList<>(listing.getCollectedLines()));
  }

  @Test
  void script() throws Exception {
    Listing listing = new Listing();
    listing.add("var fun1 = function(name) { return \"Hi \" + name; };").newline();
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
    engine.eval(listing.toString());
    Invocable invocable = (Invocable) engine;
    assertEquals("Hi Bob", invocable.invokeFunction("fun1", "Bob"));
  }

  @Test
  void trim() {
    Listing listing = new Listing();
    listing.add(' ');
    listing.trim();
    assertEquals("", listing.toString());
    listing.getCurrentLine().setLength(0);
    listing.add(" abc  ");
    listing.trim();
    assertEquals(" abc", listing.toString());
    listing.getCurrentLine().setLength(0);
    listing.add("abcef").newline().newline().newline();
    listing.trim();
    assertEquals("abcef\n", listing.toString());
  }
}
