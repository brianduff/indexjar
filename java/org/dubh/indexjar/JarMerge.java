package org.dubh.indexjar;

import org.dubh.engage.ConfigurationEngine;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.concurrent.*;

class JarMerge {
  private static final int THREADS = 12;
  // Usage: JarMerge --count=<count> path:separated:jar:list
  public static void main(String[] args) throws Exception {
    ConfigurationEngine config = new ConfigurationEngine()
        .withCommandlineArgs(args)
        .withGeneratedProperties(Flags.get())
        .initialize();
    if (!config.checkUsage()) {
      System.exit(1);
    }

    JarMerge merge = new JarMerge(Flags.get().getCount(), Paths.get(Flags.get().getOutdir()), config.getUnprocessedArgs().get(0));
    merge.merge();
  }

  private final int count;
  private final Path outDir;
  private final List<String> jars;

  JarMerge(int count, Path outDir, String jars) {
    this.count = count;
    this.outDir = outDir;
    this.jars = Arrays.asList(jars.split(":"));
  }

  public void merge() throws IOException {
    ExecutorService executor = Executors.newFixedThreadPool(THREADS);
    int totalJars = jars.size();
    int jarsPerShard = totalJars / count;
    for (int i = 0; i < count; i++) {
      int startIndex = jarsPerShard * i;
      int endIndex = startIndex + jarsPerShard;
      // Be sure to include all jars in the last shard
      if (i == count - 1) endIndex = jars.size() - 1;

      final int shardNumber = i;
      final int endIndexFinal = endIndex;
      executor.execute(() -> merge(String.format("%s/%s.jar", outDir, shardNumber), jars.subList(startIndex, endIndexFinal)));
    }
  }

  private void merge(String outJar, List<String> inputJars) {
    // TODO: handle metadata files that need to be merged?
    System.out.printf("Merging %s jars into %s\n", inputJars.size(), outJar);
    try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outJar))) {
      for (String inputFile : inputJars) {
        try (ZipFile inputZip = new ZipFile(inputFile)) {
          Enumeration<? extends ZipEntry> entries = inputZip.entries();
          while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.isDirectory()) continue;
            InputStream in = inputZip.getInputStream(entry);
            try {
              zos.putNextEntry(entry);
              transferTo(in, zos);
              zos.closeEntry();
            } catch (ZipException e) {
              //System.err.println("Skipped: " + entry);
            }
          }  
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      System.out.printf("Wrote %s\n", outJar);
    }
  }

  private static long transferTo(InputStream in, OutputStream out) throws IOException {
    long transferred = 0;
    byte[] buffer = new byte[8192];
    int read;
    while ((read = in.read(buffer, 0, 8192)) >= 0) {
        out.write(buffer, 0, read);
        transferred += read;
    }
    return transferred;
}
}
