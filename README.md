# kollections

[![CI](https://github.com/ajthom90/kollections/actions/workflows/ci.yml/badge.svg)](https://github.com/ajthom90/kollections/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A small [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) library of collection
types and helpers — **multimaps**, **multisets**, and **tables** — plus a handful of `Iterable`,
`List`, `Map`, and `Set` conveniences. The API is intentionally modeled on
[Google Guava](https://github.com/google/guava) so that it feels familiar, but the implementations are
original code written against the Kotlin standard library (no Guava source is included).

## Targets

The library is published for every target declared in `build.gradle.kts`, including the JVM, JS
(Node.js), and a wide range of Kotlin/Native targets (macOS, iOS, watchOS, tvOS, Linux, Windows, and
Android Native).

## Installation

Artifacts are published to **Maven Central** under `io.github.ajthom90:kollections` — no
authentication required to consume.

`build.gradle.kts`:

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.ajthom90:kollections:1.0.22")
}
```

In a multiplatform project add it to the relevant source set (e.g. `commonMain`) instead.

## Usage

### Multimap

A map from a key to a list of values.

```kotlin
import dev.ajthom.kollections.multimap.*

// From an existing map
val byLetter = multimapOf(mapOf("a" to listOf(1, 2), "b" to listOf(3)))

// Built imperatively
val grouped = buildMultimap<String, Int> {
    put("a", 1)
    put("a", 2)
    putAll("b", listOf(3, 4))
}

// Grouped from a collection
val words = listOf("apple", "avocado", "banana")
val byFirstChar = words.multimapWith { it.first() }   // a -> [apple, avocado], b -> [banana]

byFirstChar['a']            // [apple, avocado]
byFirstChar.containsKey('b')
byFirstChar.flatValues      // every value in a single list
```

### Table

A two-dimensional map keyed by a row and a column (like a sparse grid).

```kotlin
import dev.ajthom.kollections.table.*

val table = buildTable<String, String, Int> {
    put("2023", "Q1", 10)
    put("2023", "Q2", 20)
    put("2024", "Q1", 15)
}

table.get("2023", "Q2")   // 20
table.row("2023")         // {Q1=10, Q2=20}
table.column("Q1")        // {2023=10, 2024=15}
table.transpose()         // rows and columns swapped
```

### Guava-style helpers

```kotlin
import dev.ajthom.kollections.iterable.Iterables
import dev.ajthom.kollections.list.ImmutableList
import dev.ajthom.kollections.map.ImmutableMap
import dev.ajthom.kollections.map.orEmpty
import dev.ajthom.kollections.set.Sets

ImmutableList.of(1, 2, 3)
ImmutableMap.of("k", "v")
Iterables.getFirst(listOf(1, 2), default = 0)
Iterables.only(listOf(42))               // throws if there is more than one element
Sets.intersection(setOf(1, 2, 3), setOf(2, 3, 4))   // {2, 3}

val maybeMap: Map<String, Int>? = null
maybeMap.orEmpty()                        // {}
```

## Building

```bash
./gradlew build        # compile + test all targets (run on macOS for the Apple targets)
./gradlew jvmTest      # JVM tests only
```

Releases go to Maven Central via the Sonatype Central Portal. Publishing is automated by
[`.github/workflows/publish.yml`](.github/workflows/publish.yml): create a GitHub Release and it runs
`./gradlew publishAndReleaseToMavenCentral` using the `MAVEN_CENTRAL_*` and `SIGNING_*` repository
secrets. To publish locally instead, provide the same values as Gradle properties:

```bash
./gradlew publishAndReleaseToMavenCentral \
  -PmavenCentralUsername=*** -PmavenCentralPassword=*** \
  -PsigningInMemoryKey="$(gpg --armor --export-secret-keys <KEY_ID>)" \
  -PsigningInMemoryKeyPassword=***
```

## License

[MIT](LICENSE) © Andrew J. Thom. API design inspired by [Google Guava](https://github.com/google/guava)
(Apache-2.0); no Guava source code is included.
