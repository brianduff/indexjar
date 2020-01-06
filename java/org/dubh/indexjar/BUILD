java_library(
    name = "indexjar",
    srcs = glob(["*.java"]),
    deps = ["@maven//:com_google_guava_guava"],
)

java_binary(
    name = "IndexJar",
    main_class = "org.dubh.indexjar.IndexJar",
    runtime_deps = [":indexjar"],
)
