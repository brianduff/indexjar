workspace(name="indexjar")
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")



RULES_JVM_EXTERNAL_TAG = "3.0"
RULES_JVM_EXTERNAL_SHA = "62133c125bf4109dfd9d2af64830208356ce4ef8b165a6ef15bbff7460b35c3a"

http_archive(
    name = "rules_jvm_external",
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    sha256 = RULES_JVM_EXTERNAL_SHA,
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % RULES_JVM_EXTERNAL_TAG,
)

load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    artifacts = [
        "com.google.guava:guava:19.0",
        "org.json:json:20180813",
        "com.github.spullara.mustache.java:compiler:0.9.6",
        "org.hjson:hjson:3.0.0",
    ],
    repositories = [
        "https://maven.google.com",
        "https://repo1.maven.org/maven2",
    ],
)

http_archive(
    name = "engage",
    urls = ["https://github.com/brianduff/engage/archive/1.0.zip"],
    sha256 = "277eea10f97c647df13330626b61cf6f795832913693cfe3a30555e45eb97f1c",
    strip_prefix = "engage-1.0"
)