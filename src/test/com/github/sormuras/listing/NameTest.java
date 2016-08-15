package com.github.sormuras.listing;

import static com.github.sormuras.listing.Compilation.compile;
import static com.github.sormuras.listing.Name.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.expectThrows;

import com.github.sormuras.listing.unit.CompilationUnit;
import com.github.sormuras.listing.unit.NormalClassDeclaration;
import java.lang.annotation.ElementType;
import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Test;

class NameTest {

  @Test
  void apply() {
    assertEquals("byte", of(byte.class).apply(new Listing()).toString());
  }

  @Test
  void cast() throws Exception {
    Name name = of(Objects.class, "hash");
    assertNull(Name.cast(null));
    assertSame(name, Name.cast(name));
    assertEquals(of(Object.class), Name.cast(Object.class));
    assertEquals(of(Thread.State.BLOCKED), Name.cast(Thread.State.BLOCKED));
    assertEquals(of("abc", "X"), Name.cast(new String[] {"abc", "X"}));
    assertEquals(of(Math.class.getField("PI")), Name.cast(Math.class.getField("PI")));
    expectThrows(IllegalArgumentException.class, () -> Name.cast(BigInteger.ZERO));
  }

  @Test
  void enclosing() {
    assertEquals(Optional.empty(), of(byte.class).getEnclosing());
    assertEquals(Optional.empty(), of("a").getEnclosing());
    assertEquals(Optional.empty(), of("java").getEnclosing());
    assertEquals(of("java"), of("java.lang").getEnclosing().get());
    assertEquals(of("java.lang"), of(Object.class).getEnclosing().get());
    assertEquals(of("java.lang"), of(Thread.class).getEnclosing().get());
    assertEquals(of(Thread.class), of(Thread.State.class).getEnclosing().get());
    assertEquals(of(Objects.class), of(Objects.class, "requireNonNull").getEnclosing().get());
  }

  @Test
  void field() {
    assertEquals("java.lang.Math.PI", of(Math.class, "PI").getCanonicalName());
    expectThrows(Error.class, () -> of(Object.class, "PI"));
    expectThrows(Error.class, () -> of(Class.class, "PO"));
  }

  @Test
  void equalsAndHashcode() {
    assertEquals(of(byte.class), of("", "byte"));
    assertEquals(of(Object.class), of("java.lang", "Object"));
    assertEquals(of(Objects.class), of("java.util", "Objects"));
    assertEquals(of(Thread.class), of("java.lang", "Thread"));
    assertEquals(of(Thread.State.class), of("java.lang", "Thread", "State"));
    // same instance
    Name integer = of(int.class);
    assertEquals(integer, integer);
    // falsify
    assertFalse(of(byte.class).equals(null));
    assertFalse(of(byte.class).equals(byte.class));
    assertFalse(of(byte.class).equals(of("a", "byte")));
  }

  @Test
  void simpleNamesJoined() {
    assertEquals("Object", simpleNamesJoined(of(Object.class)));
    assertEquals("byte", simpleNamesJoined(of(byte.class)));
    assertEquals("Object[]", simpleNamesJoined(of(Object[].class)));
    assertEquals("Object[][]", simpleNamesJoined(of(Object[][].class)));
    assertEquals(Name.class.getSimpleName(), simpleNamesJoined(of(Name.class)));
    assertEquals("Character.Subset", simpleNamesJoined(of(Character.Subset.class)));
    assertEquals("Thread.State", simpleNamesJoined(of(Thread.State.class)));
  }

  private String simpleNamesJoined(Name name) {
    return String.join(".", name.getSimpleNames());
  }

  @Test
  void packageName() {
    assertEquals("", of("", "Empty").getPackageName());
    assertEquals("", of(compile("public class Nopack {}")).getPackageName());
  }

  @Test
  void invalidIdentifier() {
    Exception e = expectThrows(IllegalArgumentException.class, () -> of());
    assertTrue(e.toString().contains("non-empty names array expected"));
  }

  @Test
  void invalidIdentifierName() {
    Exception e = expectThrows(IllegalArgumentException.class, () -> of("test", "123"));
    assertTrue(e.toString().contains("123"));
  }

  @Test
  void isJavaLangObject() throws Exception {
    assertEquals(true, of(Object.class).isJavaLangObject());
    assertEquals(false, of(Optional.class).isJavaLangObject());
    assertEquals(false, of("", "A").isJavaLangObject());
  }

  @Test
  void isJavaLangPackage() throws Exception {
    assertEquals(true, of(Object.class).isJavaLangPackage());
    assertEquals(false, of(Optional.class).isJavaLangPackage());
    assertEquals(false, of("", "A").isJavaLangPackage());
  }

  @Test
  void modified() throws Exception {
    assertTrue(of(Object.class).isModified()); // public
    assertTrue(of(Thread.State.NEW).isModified()); // public static final
    Name name = of(getClass().getDeclaredMethod("modified"));
    assertFalse(name.isModified()); // <empty>
    name.addModifier(Modifier.SYNCHRONIZED);
    assertTrue(name.isModified()); // synchronized
  }

  @Test
  void isStatic() throws Exception {
    assertEquals(false, of(Object.class).isStatic());
    assertEquals(false, of(Object.class.getDeclaredConstructor()).isStatic());
    assertEquals(false, of(Object.class.getDeclaredMethod("toString")).isStatic());
    assertEquals(true, of(Object.class.getDeclaredMethod("registerNatives")).isStatic());
    assertEquals(false, of(Object.class.getDeclaredMethod("clone")).isStatic());
    assertEquals(false, of(Thread.class.getDeclaredMethod("start")).isStatic());
    assertEquals(true, of(Math.class.getDeclaredField("PI")).isStatic());
    assertEquals(false, of(byte.class).isStatic());
    assertEquals(true, of(Character.Subset.class).isStatic());
    assertEquals(true, of(Thread.State.class).isStatic());
    assertEquals(true, of(Thread.State.NEW).isStatic());
  }

  @Test
  void list() {
    assertEquals("a.b.X", of("a.b", "X").list());
  }

  @Test
  void target() {
    assertEquals(ElementType.PACKAGE, of("a.b").getTarget().get());
    assertEquals(ElementType.TYPE, of("a.b", "X").getTarget().get());
    assertEquals(false, of("a.b", "X", "member").getTarget().isPresent());
    assertEquals(ElementType.TYPE, of(Object.class).getTarget().get());
    assertEquals(ElementType.TYPE, of(byte.class).getTarget().get());
    assertEquals(ElementType.TYPE, of(Object[].class).getTarget().get());
    assertEquals(ElementType.TYPE, of(Object[][].class).getTarget().get());
    assertEquals(ElementType.TYPE, of(Name.class).getTarget().get());
    assertEquals(ElementType.TYPE, of(Character.Subset.class).getTarget().get());
    assertEquals(ElementType.TYPE, of(Thread.State.class).getTarget().get());
    assertEquals(ElementType.FIELD, of(Thread.State.NEW).getTarget().get());
  }

  @Test
  void testToString() {
    assertEquals("Name{a.b.X, target=Optional[TYPE], modifiers=[]}", of("a.b", "X").toString());
    testToString(of(Object.class), "java.lang.Object", "public");
    testToString(of(byte.class), "byte", "public, abstract, final");
    testToString(of(Object[].class), "Object[]", "public, abstract, final");
    testToString(of(Object[][].class), "Object[][]", "public, abstract, final");
    testToString(of(Name.class), Name.class.getCanonicalName(), "public");
    testToString(of(Character.Subset.class), "java.lang.Character.Subset", "public, static");
    testToString(of(Thread.State.class), "java.lang.Thread.State", "public, static, final");
    testToString(
        of(Thread.State.NEW),
        "java.lang.Thread.State.NEW",
        ElementType.FIELD,
        "public, static, final");
    testToString(NormalClassDeclaration.of("Abc").toName(), "Abc", "");
    testToString(CompilationUnit.of("abc").declareClass("Abc").toName(), "abc.Abc", "");
  }

  private void testToString(Name name, String canonical, String modifiers) {
    testToString(name, canonical, ElementType.TYPE, modifiers);
  }

  private void testToString(Name name, String canonical, ElementType type, String modifiers) {
    assertEquals(
        "Name{" + canonical + ", target=Optional[" + type + "], modifiers=[" + modifiers + "]}",
        name.toString());
  }
}
