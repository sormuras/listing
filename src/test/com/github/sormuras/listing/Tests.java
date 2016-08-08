package com.github.sormuras.listing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;

public interface Tests {

  static void assertEquals(Class<?> testClass, String testName, Listable listable) {
    assertEquals(testClass, testName, new Listing().add(listable));
  }

  static void assertEquals(Class<?> testClass, String testName, Listing listing) {
    try {
      Assertions.assertEquals(load(testClass, testName), listing.toString());
    } catch (Exception e) {
      Assertions.fail(e.toString());
    }
  }

  static void assertSerializable(Listable listable) {
    try {
      String expected = listable.list();
      Assertions.assertEquals(expected, listable.list());
      String actual = ((Listable) convertFromBytes(convertToBytes(listable))).list();
      Assertions.assertEquals(expected, actual);
    } catch (Exception e) {
      Assertions.fail(e.toString());
    }
  }

  static Object convertFromBytes(byte[] bytes) throws Exception {
    try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = new ObjectInputStream(bis)) {
      return in.readObject();
    }
  }

  static byte[] convertToBytes(Object object) throws Exception {
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bos)) {
      out.writeObject(object);
      return bos.toByteArray();
    }
  }

  static String load(Class<?> testClass, String testName) {
    String fileName = testClass.getName().replace('.', '/') + "." + testName + ".txt";
    try {
      Path path = Paths.get(testClass.getClassLoader().getResource(fileName).toURI());
      return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new AssertionError("Loading `" + fileName + "` failed!", e);
    }
  }

  static <T> T proxy(Class<T> type, InvocationHandler handler) {
    ClassLoader loader = type.getClassLoader();
    Class<?>[] interfaces = {type};
    return type.cast(Proxy.newProxyInstance(loader, interfaces, handler));
  }
}
