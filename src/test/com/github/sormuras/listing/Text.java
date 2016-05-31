package com.github.sormuras.listing;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;

public interface Text {

  static <T> T proxy(Class<T> type, InvocationHandler handler) {
    ClassLoader loader = type.getClassLoader();
    Class<?>[] interfaces = {type};
    return type.cast(Proxy.newProxyInstance(loader, interfaces, handler));
  }

  static void assertEquals(Class<?> testClass, String testName, Listable listable) {
    assertEquals(testClass, testName, new Listing().add(listable));
  }

  static void assertEquals(Class<?> testClass, String testName, Listing listing) {
    try {
      Assert.assertEquals(load(testClass, testName), listing.toString());
    } catch (Exception e) {
      Assert.fail(e.toString());
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
}
