package org.dubh.indexjar;

import org.dubh.engage.ConfigurationEngine;

class JarMerge {
  public static void main(String[] args) {
    ConfigurationEngine ce = new ConfigurationEngine()
        .withCommandlineArgs(args)
        .withGeneratedProperties(Flags.get())
        .initialize();
    if (!ce.checkUsage()) {
      System.exit(1);
    }
  }
}
