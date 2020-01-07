load("@engage//generator:engage.bzl", "engage_java_library")

java_library(
    name = "indexjar",
    srcs = glob(["*.java"]),
    deps = ["@maven//:com_google_guava_guava", "@engage//runtime", ":Flags"],
)

java_binary(
    name = "IndexJar",
    main_class = "org.dubh.indexjar.IndexJar",
    runtime_deps = [":indexjar"],
)

java_binary(
    name = "TestIndexReader",
    main_class = "org.dubh.indexjar.IndexReader",
    runtime_deps = [":indexjar"],
)

java_binary(
    name = "JarMerge",
    main_class = "org.dubh.indexjar.JarMerge",
    runtime_deps = [":indexjar"],
)


engage_java_library(
    name = "Flags",
    java_package = "org.dubh.indexjar",
    properties = ":flags.hjson"
)