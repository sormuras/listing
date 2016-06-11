package de.codeturm.listing;

import static java.lang.Math.PI;
import static java.util.Arrays.asList;
import static java.util.Locale.GERMAN;
import static org.junit.Assert.assertEquals;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.Test;

public class LinesTest {

  @Test
  public void addChar() {
    assertEquals("\0", new Lines().add('\0').toString());
    //assertEquals("'0'", new Lines().add(Listable.of('0')).toString());
    //assertEquals("'\\u0000'", new Lines().add(Listable.of('\0')).toString());
  }

  @Test
  public void addFormattedString() {
    assertEquals("abc", new Lines().add("a%sc", "b").toString());
    assertEquals("abc", new Lines().add("abc", new Object[0]).toString());
    assertEquals("3,14159", new Lines().add(GERMAN, "%.5f", PI).toString());
    assertEquals("3,14159", new Lines().add(GERMAN, "3,14159").toString());
  }

  @Test
  public void beginAndEnd() {
    Lines lines = new Lines();
    lines.add("BEGIN").newline();
    lines.add("END.").newline();
    assertEquals("BEGIN\nEND.\n", lines.toString());
    assertEquals(asList("BEGIN", "END."), lines.getCollectedLines());
  }

  @Test
  public void defaults() {
    Lines lines = new Lines();
    assertEquals(0, lines.getIndentationDepth());
    assertEquals("  ", lines.getIndentationString());
    assertEquals("\n", lines.getLineSeparator());
  }

  @Test
  public void indent() {
    Lines lines = new Lines();
    lines.add("BEGIN").newline();
    lines.indent(1).add("writeln('Hello, world.')").newline().indent(-1);
    lines.add("END.").newline();
    assertEquals(0, lines.getIndentationDepth());
    assertEquals(Fixtures.text(getClass(), "indent"), lines.toString());
    lines.indent(-100);
    assertEquals(0, lines.getIndentationDepth());
  }

  @Test
  public void newlineProducesOnlyOneSingleBlankLine() {
    Lines lines = new Lines();
    assertEquals(0, lines.getCurrentLine().length());
    lines.newline().newline().newline().newline().newline();
    assertEquals(0, lines.getCurrentLine().length());
    lines.add("BEGIN").newline().newline().newline().newline().newline();
    assertEquals(0, lines.getCurrentLine().length());
    lines.add("END.").newline().newline().newline().newline().newline();
    assertEquals(0, lines.getCurrentLine().length());
    assertEquals("BEGIN\n\nEND.\n\n", lines.toString());
    assertEquals(0, lines.getCurrentLine().length());
    assertEquals(asList("BEGIN", "", "END.", ""), lines.getCollectedLines());
  }

  @Test
  public void script() throws Exception {
    Lines lines = new Lines();
    lines.add("var fun1 = function(name) { return \"Hi \" + name; };").newline();
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
    engine.eval(lines.toString());
    Invocable invocable = (Invocable) engine;
    assertEquals("Hi Bob", invocable.invokeFunction("fun1", "Bob"));
  }
}
