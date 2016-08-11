package com.github.sormuras.listing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import org.junit.jupiter.api.Assertions;

public interface Tests {

  @SafeVarargs
  static <T> Deque<T> asDeque(T... elements) {
    return new ArrayDeque<>(Arrays.asList(elements));
  }

  static void assertEquals(Class<?> testClass, String testName, Listable listable) {
    assertEquals(testClass, testName, listable.list());
  }

  static void assertEquals(Class<?> testClass, String testName, Listing listing) {
    assertEquals(testClass, testName, listing.toString());
  }

  static void assertEquals(Class<?> testClass, String testName, String actual) {
    try {
      Assertions.assertEquals(load(testClass, testName), actual);
    } catch (Exception e) {
      Assertions.fail(e.toString());
    }
  }

  static void assertSerializable(Listable listable) {
    try {
      String expected = listable.list();
      Assertions.assertEquals(expected, listable.list());
      byte[] bytes = convertToBytes(listable);
      Object converted = convertFromBytes(bytes);
      String actual = ((Listable) converted).list();
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

  /** Same as: <code>toString(bytes, 16, 16)</code>. */
  static String toString(byte[] bytes) {
    return toString(bytes, 16, 16);
  }

  /** Same as: <code>toString(ByteBuffer.wrap(bytes), bytesPerLine, maxLines)</code>. */
  static String toString(byte[] bytes, int bytesPerLine, int maxLines) {
    return toString(ByteBuffer.wrap(bytes), bytesPerLine, maxLines);
  }

  /** Same as: <code>toString(buffer, 16, 16)</code>. */
  static String toString(ByteBuffer buffer) {
    return toString(buffer, 16, 16);
  }

  static String toString(ByteBuffer buffer, int bytesPerLine, int maxLines) {
    return toString(new StringBuilder(), buffer, bytesPerLine, maxLines);
  }

  static String toString(StringBuilder builder, ByteBuffer buffer) {
    return toString(builder, buffer, 16, 16);
  }

  static String toString(StringBuilder builder, ByteBuffer buffer, int bytesPerLine, int maxLines) {
    final boolean INCLUDE_SEGMENT_NUMBERS = true;
    final boolean INCLUDE_VIEW_HEX = true;
    final boolean INCLUDE_VIEW_ASCII = true;
    final int BLOCK_LENGTH = 4;
    final char BLOCK_SEPARATOR = ' ';
    int i, j, n, k, line;
    builder.append(buffer).append(" {\n");
    line = 0;
    for (n = 0; n < buffer.remaining(); n += bytesPerLine, line++) {
      // builder.append(" ");
      if (line >= maxLines) {
        int omitted = buffer.remaining() - n;
        builder.append("...(");
        builder.append(omitted);
        builder.append(" byte");
        builder.append(omitted != 1 ? "s" : "");
        builder.append(" omitted)\n");
        break;
      }
      if (INCLUDE_SEGMENT_NUMBERS) {
        String segment = Integer.toHexString(n).toUpperCase();
        for (j = 0, k = 4 - segment.length(); j < k; j++) {
          builder.append('0');
        }
        builder.append(segment).append(" | ");
      }
      if (INCLUDE_VIEW_HEX) {
        for (i = n; i < n + bytesPerLine && i < buffer.remaining(); i++) {
          String s = Integer.toHexString(buffer.get(i) & 255).toUpperCase();
          if (s.length() == 1) {
            builder.append('0');
          }
          builder.append(s).append(' ');
          if (i % bytesPerLine % BLOCK_LENGTH == BLOCK_LENGTH - 1 && i < n + bytesPerLine - 1) {
            builder.append(BLOCK_SEPARATOR);
          }
        }
        while (i < n + bytesPerLine) {
          builder.append("   ");
          if (i % bytesPerLine % BLOCK_LENGTH == BLOCK_LENGTH - 1 && i < n + bytesPerLine - 1) {
            builder.append(BLOCK_SEPARATOR);
          }
          i++;
        }
        builder.append('|').append(' ');
      }
      if (INCLUDE_VIEW_ASCII) {
        for (i = n; i < n + bytesPerLine && i < buffer.remaining(); i++) {
          int v = buffer.get(i) & 255;
          if (v > 127 || Character.isISOControl((char) v)) {
            builder.append('.');
          } else {
            builder.append((char) v);
          }
          if (i % bytesPerLine % BLOCK_LENGTH == BLOCK_LENGTH - 1 && i < n + bytesPerLine - 1) {
            builder.append(BLOCK_SEPARATOR);
          }
        }
        while (i < n + bytesPerLine) {
          builder.append(' ');
          if (i % bytesPerLine % BLOCK_LENGTH == BLOCK_LENGTH - 1 && i < n + bytesPerLine - 1) {
            builder.append(BLOCK_SEPARATOR);
          }
          i++;
        }
      }
      builder.append('\n');
    }
    builder.append("}");
    return builder.toString();
  }
}
