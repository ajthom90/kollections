# kollections

[![CI](https://github.com/ajthom90/kollections/actions/workflows/ci.yml/badge.svg)](https://github.com/ajthom90/kollections/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.ajthom90/kollections.svg)](https://central.sonatype.com/artifact/io.github.ajthom90/kollections)
[![API reference](https://img.shields.io/badge/docs-API%20reference-blue)](https://ajthom90.github.io/kollections/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A small [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) library of collection
types and helpers — **multimaps**, **multisets**, **tables**, and **bimaps** — plus a handful of
`Iterable`, `List`, `Map`, and `Set` conveniences. The API is modeled on
[Google Guava](https://github.com/google/guava) so that it feels familiar, with Kotlin-style
read-only/mutable interfaces and stdlib-style factories. The implementations are original code
written against the Kotlin standard library (no Guava source is included).

Version 2.0 is a breaking revision of 1.x; see [CHANGELOG.md](CHANGELOG.md) for the migration table.

## Targets

The library is published for every target declared in `build.gradle.kts`: JVM, JS (Node.js),
Wasm/JS, and a wide range of Kotlin/Native targets (macOS, iOS, watchOS, tvOS, Linux, Windows, and
Android Native).

## Installation

Artifacts are published to **Maven Central** under `io.github.ajthom90:kollections`.

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.ajthom90:kollections:2.0.0")
}
```

In a multiplatform project add it to the relevant source set (e.g. `commonMain`) instead.

## Usage

### Multimap

A map from a key to a collection of values. `ListMultimap` keeps duplicates and insertion order;
`SetMultimap` keeps each key/value pair once.

```kotlin
import dev.ajthom.kollections.multimap.*

val byLetter = multimapOf("a" to 1, "a" to 2, "b" to 3)     // ListMultimap<String, Int>
byLetter["a"]            // [1, 2]
byLetter["z"]            // []
byLetter.size            // 3 key-value pairs
byLetter.keys            // [a, b]
byLetter.flatValues      // [1, 2, 3]
byLetter.containsEntry("a", 2)

for ((key, value) in byLetter) { /* one pair at a time */ }

val tags = setMultimapOf("post" to "kotlin", "post" to "kotlin")   // SetMultimap
tags.size                // 1

val grouped = buildMultimap<String, Int> {
    put("a", 1)
    putAll("b", listOf(2, 3))
}

val words = listOf("apple", "avocado", "banana")
val byFirstChar = words.multimapWith { it.first() }       // a -> [apple, avocado], b -> [banana]

byFirstChar.inverse()                                     // apple -> [a], ...
byFirstChar.filterKeys { it == 'a' }
byFirstChar.transformValues { it.length }
byFirstChar + ('c' to "cherry")
```

Mutable multimaps expose **live views**: adding to `multimap[key]` inserts into the multimap, and
removing the last value through the view drops the key.

```kotlin
val mm = mutableMultimapOf<String, Int>()
mm["a"].add(1)            // inserts a -> [1]
mm.put("a", 2)
mm.remove("a", 1)         // true
mm.replaceValues("b", listOf(7, 8))
mm += "c" to 9
mm.keys.remove("b")       // drops every value under "b"
```

### Table

A two-dimensional map keyed by a row and a column (a sparse grid).

```kotlin
import dev.ajthom.kollections.table.*

val table = buildTable<String, String, Int> {
    put("2023", "Q1", 10)
    put("2023", "Q2", 20)
    put("2024", "Q1", 15)
}

table["2023", "Q2"]       // 20
table.row("2023")         // {Q1=10, Q2=20}
table.column("Q1")        // {2023=10, 2024=15}
table.size                // 3 cells
table.transpose()         // rows and columns swapped
table.mapValues { it * 2 }
table.filter { (row, _, value) -> row == "2023" && value > 10 }

val sales = mutableTableOf<String, String, Int>()
sales["2025", "Q1"] = 30            // set operator
sales.row("2025")["Q2"] = 40        // live row view
```

### Multiset

A collection that counts occurrences.

```kotlin
import dev.ajthom.kollections.multiset.*

val bag = multisetOf("a", "b", "a")
bag.count("a")            // 2
bag.size                  // 3
bag.elementSet            // [a, b]
bag.entries               // [Entry(element=a, count=2), Entry(element=b, count=1)]

val counts = mutableMultisetOf<String>()
counts.add("x", 3)
counts.remove("x")        // removes one occurrence
counts.setCount("x", 0)   // removes it entirely
```

### BiMap

A map whose values are unique, with an inverse view.

```kotlin
import dev.ajthom.kollections.bimap.*

val codes = biMapOf("US" to 1, "CA" to 2)
codes.inverse()[2]        // "CA"

val mutable = mutableBiMapOf<String, Int>()
mutable["US"] = 1
mutable["MX"] = 1         // throws IllegalArgumentException: value already bound to "US"
mutable.forcePut("MX", 1) // rebinds 1 to "MX" and drops "US"
```

### Guava-style helpers

```kotlin
import dev.ajthom.kollections.iterable.Iterables
import dev.ajthom.kollections.list.ImmutableList
import dev.ajthom.kollections.map.ImmutableMap
import dev.ajthom.kollections.map.difference
import dev.ajthom.kollections.set.ImmutableSet
import dev.ajthom.kollections.set.Sets

ImmutableList.of(1, 2, 3)
ImmutableSet.of("a", "b")
ImmutableMap.of("k", "v")
Iterables.getFirst(listOf(1, 2), default = 0)
Iterables.only(listOf(42))                          // throws if there is more than one element

Sets.intersection(setOf(1, 2, 3), setOf(2, 3, 4))   // {2, 3}
Sets.symmetricDifference(setOf(1, 2), setOf(2, 3))  // {1, 3}
Sets.powerSet(setOf(1, 2))                           // {{}, {1}, {2}, {1, 2}}
Sets.combinations(setOf(1, 2, 3), 2)                 // {{1, 2}, {1, 3}, {2, 3}}
Sets.cartesianProduct(setOf(1, 2), setOf("a", "b"))  // {[1, a], [1, b], [2, a], [2, b]}

val diff = mapOf("a" to 1, "b" to 2).difference(mapOf("b" to 3, "c" to 4))
diff.entriesOnlyOnLeft    // {a=1}
diff.entriesDiffering     // {b=ValueDifference(leftValue=2, rightValue=3)}
```

## Building

```bash
./gradlew build        # compile + test all targets, API check, ktlint (run on macOS for the Apple targets)
./gradlew jvmTest      # JVM tests only
./gradlew apiDump      # refresh api/kollections.api after an intentional public API change
./gradlew ktlintFormat # fix formatting
```

The public API is tracked in [`api/kollections.api`](api/kollections.api) by the Kotlin
binary-compatibility validator; CI fails if it changes without an `apiDump`.

## Releasing

The version lives in one place: `VERSION_NAME` in `gradle.properties`. To release:

1. Bump `VERSION_NAME`, update `CHANGELOG.md`, and merge to `master`.
2. Create a GitHub Release with tag `v<VERSION_NAME>` (for example `v2.0.0`).

[`.github/workflows/publish.yml`](.github/workflows/publish.yml) verifies the tag matches
`VERSION_NAME` and runs `./gradlew publishAndReleaseToMavenCentral` using the `MAVEN_CENTRAL_*`
and `SIGNING_*` repository secrets. To publish locally instead, provide the same values as Gradle
properties:

```bash
./gradlew publishAndReleaseToMavenCentral \
  -PmavenCentralUsername=*** -PmavenCentralPassword=*** \
  -PsigningInMemoryKey="$(gpg --armor --export-secret-keys <KEY_ID>)" \
  -PsigningInMemoryKeyPassword=***
```

The API reference is generated by Dokka and published to
[ajthom90.github.io/kollections](https://ajthom90.github.io/kollections/) by
[`.github/workflows/docs.yml`](.github/workflows/docs.yml) on every push to `master`
(one-time setup: repository Settings -> Pages -> Source: GitHub Actions).

## License

[MIT](LICENSE) © Andrew J. Thom. API design inspired by [Google Guava](https://github.com/google/guava)
(Apache-2.0); no Guava source code is included.
