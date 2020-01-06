package org.dubh.indexjar;

import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.util.stream.*;
import com.google.common.base.Splitter;
import com.google.common.collect.Multimap;
import com.google.common.collect.LinkedHashMultimap;
import java.nio.charset.StandardCharsets;
import java.util.zip.*;

public class IndexJar {
  private static final Splitter PATH_SPLITTER = Splitter.on(":");

  private final List<String> entries;

  IndexJar(List<String> entries) {
    this.entries = entries;
  }

  private void generate() throws IOException {
    Multimap<String, String> pathToMatchingJars = LinkedHashMultimap.create();
    for (String jarFile : entries) {
      File file = new File(jarFile);
      if (file.isFile()) {
        Set<String> dirs = getDirectoriesContainingClasses(new ZipFile(file));
        for (String dir : dirs) {
          pathToMatchingJars.put(dir, jarFile);
        }
      }
    }

    try (PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)))) {
      for (String key : pathToMatchingJars.keySet()) {
        pw.println(key);
        for (String value : pathToMatchingJars.get(key)) {
          pw.println("  " + value);
        }
      }
    }
  }

  private Set<String> getDirectoriesContainingClasses(ZipFile zipFile) {
    Set<String> directories = new LinkedHashSet<>();
    Enumeration<? extends ZipEntry> entries = zipFile.entries();
    while (entries.hasMoreElements()) {
      ZipEntry entry = entries.nextElement();
      String name = entry.getName();
      if (name.endsWith(".class")) {
        String dirName = entry.getName().substring(0, name.lastIndexOf('/'));
        directories.add(dirName);
      }
    }
    return directories;
  }

  public static void main(String[] args) throws Exception {
    // Support reading classpath entries from an @ file.
    List<String> allEntries = new ArrayList<>(1000);
    for (String arg : args) {
      if (arg.length() > 1 && arg.charAt(0) == '@') {
        loadEntriesFromFile(Paths.get(arg.substring(1)), allEntries);
      } else {
        allEntries.addAll(PATH_SPLITTER.splitToList(arg));
      }
    }
    new IndexJar(allEntries).generate();
  }

  private static void loadEntriesFromFile(Path file, List<String> allEntries) throws IOException {
    try (Stream<String> stream = Files.lines(file, StandardCharsets.UTF_8)) {
      stream.forEach(s -> allEntries.addAll(PATH_SPLITTER.splitToList(s)));
    }
  }
}