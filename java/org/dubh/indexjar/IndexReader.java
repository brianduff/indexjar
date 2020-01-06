package org.dubh.indexjar;

import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.util.stream.*;
import java.nio.charset.StandardCharsets;

// Reads the index into a Map of Lists (no guava deps)
public class IndexReader {
  public Map<String, List<String>> read(String indexText) throws IOException {
    Context context = new Context();
    try (Stream<String> stream = Files.lines(Paths.get(indexText), StandardCharsets.UTF_8)) {
      stream.forEach(s -> processLine(context, s));
    }
    return context.map;
  }

  private void processLine(Context context, String line) {
    if (line.charAt(0) == ' ') {
      if (context.currentDirectory == null) {
        throw new IllegalStateException("Invalid line: " + line);
      }
      context.map.get(context.currentDirectory).add(line.substring(2));
    } else {
      context.currentDirectory = line;
      context.map.put(line, new ArrayList<String>());
    }
  }

  public static void main(String[] args) throws IOException {
    IndexReader r = new IndexReader();
    System.out.println(r.read(args[0]));
  }

  private class Context {
    private Map<String, List<String>> map = new HashMap<>();
    private String currentDirectory;
  }
}