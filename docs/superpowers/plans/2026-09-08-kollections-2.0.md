# kollections 2.0 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Ship kollections 2.0.0: correct live views and equality, SetMultimap, Multiset, BiMap, MapDifference, Sets helpers, idiomatic factories, and a hardened library build.

**Architecture:** Each collection family (multimap, table, multiset, bimap) gets a read-only interface, a mutable interface, a public abstract base holding equality and derived operations, concrete backed implementations, and a `Xs.kt` file of factories and extensions. Mutable types expose live views for `get`/`row`/`keys`/`asMap`/`rowMap` and snapshots elsewhere, documented per accessor.

**Tech Stack:** Kotlin Multiplatform 2.3.21, Gradle 9.5.1, kotlin.test, Dokka 2.2.0, vanniktech maven-publish, binary-compatibility-validator, ktlint.

**Spec:** `docs/superpowers/specs/2026-09-08-kollections-2.0-design.md`

## Global Constraints

- Version `2.0.0`, group `io.github.ajthom90`, artifact `kollections`.
- `explicitApi()` strict: every public declaration has `public` and an explicit return type.
- KDoc on every public declaration; state "live view" or "snapshot" on mutable-type accessors.
- 4-space indentation, no tabs, max line length 120.
- Tests: kotlin.test in `src/commonTest`, one assertion per `assertX` call, no multi-statement `assertTrue { }`.
- No commits by agents; the maintainer commits.
- Common-only code: no `java.*`, no `TreeMap`, no JVM-only APIs in `commonMain`.

---

### Task 0: Build baseline (main session)

**Files:**
- Modify: `gradle.properties`, `build.gradle.kts`, `.gitignore`
- Create: `.editorconfig`
- Delete: `src/commonMain/kotlin/dev/ajthom/kollections/set/Multiset.kt`

- [ ] `gradle.properties` gains `GROUP=io.github.ajthom90`, `POM_ARTIFACT_ID=kollections`, `VERSION_NAME=2.0.0`.
- [ ] `build.gradle.kts`: `group = property("GROUP")`, `version = property("VERSION_NAME")`, `coordinates(group, "kollections", version)`; add `explicitApi()`, `macosX64()`, `wasmJs { nodejs() }`.
- [ ] `.editorconfig`: `[*] indent_style=space, indent_size=4, end_of_line=lf, insert_final_newline=true`; `[*.{kt,kts}] max_line_length=120, ktlint_code_style=intellij_idea`; `[*.{yml,yaml}] indent_size=2`.
- [ ] `.gitignore`: add `.DS_Store`.

### Task 1: Multimap (main session)

**Files:**
- Rewrite: `src/commonMain/kotlin/dev/ajthom/kollections/multimap/{Multimap,MutableMultimap,Multimaps}.kt`
- Create: `AbstractMultimap.kt`, `AbstractMutableMultimap.kt`, `DelegatingListMultimap.kt`, `DelegatingSetMultimap.kt`, `DelegatingMutableListMultimap.kt`, `DelegatingMutableSetMultimap.kt`, `LinkedHashListMultimap.kt`, `LinkedHashSetMultimap.kt`, `Deprecated.kt`
- Delete: `DelegatingMultimap.kt`, `DelegatingMutableMultimap.kt`, `MutableLinkedHashMultimap.kt`
- Tests: `src/commonTest/kotlin/dev/ajthom/kollections/multimap/{ListMultimapTest,SetMultimapTest,MutableMultimapViewsTest,MultimapsTest}.kt` (delete `DelegatingMultimapTests.kt`)

**Produces:** the interfaces in the spec verbatim.

Live view pattern (reused by Task 2 for table rows):

```kotlin
// inside AbstractMutableMultimap
private inner class ListView(private val key: K) : AbstractMutableList<V>() {
    private fun backing(): MutableList<V>? = map[key]        // null when key absent
    override val size: Int get() = backing()?.size ?: 0
    override fun get(index: Int): V = backing()?.get(index) ?: throw IndexOutOfBoundsException(...)
    override fun add(index: Int, element: V) { map.getOrPut(key) { createCollection() }.add(index, element) }
    override fun removeAt(index: Int): V {
        val list = backing() ?: throw IndexOutOfBoundsException(...)
        val removed = list.removeAt(index)
        if (list.isEmpty()) map.remove(key)
        return removed
    }
    override fun set(index: Int, element: V): V = backing()?.set(index, element) ?: throw IndexOutOfBoundsException(...)
    override fun clear() { map.remove(key) }
}
```

Test cases (each its own `@Test`):
- ListMultimap: `get` present, `get` absent returns empty list, `size` counts pairs (`{a:[1,2], b:[3]}` -> 3), `keys`, `values`, `flatValues` order, `entries` destructure, `containsKey/Value/Entry`, `isEmpty`, `forEach` visits pairs in order, `iterator()` extension yields pairs, equality/hashCode of two equal multimaps, inequality vs SetMultimap with same content, `toString` equals `asMap().toString()`.
- SetMultimap: duplicates collapse (`setMultimapOf("a" to 1, "a" to 1).size == 1`), `get` returns Set.
- Mutable views: `mm["x"].add(1)` on absent key inserts (`containsKey("x")`); removing the last element through the view drops the key; `view.clear()` drops the key; `mm.keys.remove("a")` drops all values; `asMap()["a"]` is live (adding to it shows in `mm["a"]`); `asMap()["zzz"]` is null; `entries`, `values`, `flatValues` are snapshots (mutating the multimap afterwards does not change a captured snapshot).
- Mutations: `put` returns true (list) / false on duplicate (set); `putAll(key, iterable)`; `putAll(multimap)`; `putAll(map)`; `remove(key, value)` true/false; `remove(key)` returns removed values and empty for absent; `replaceValues` returns old values and installs new, empty iterable removes key; `clear()`; `plusAssign`/`minusAssign`.
- Factories/extensions: `emptyMultimap().isEmpty()`, `multimapOf(pairs)` order, `multimapOf(map)` copies (mutate source map afterwards; multimap unchanged), `asMultimap()` is a view (mutate source; multimap reflects), `toMultimap` from pairs/map/multimap, `toSetMultimap`, `toMutableMultimap` is independent of the source, `buildMultimap`, `buildSetMultimap`, `multimapWith`, `multimapWith(keySelector, valueTransform)`, `multimapWith(destination, keySelector)`, `transformValues`, `mapKeys` merges collisions, `filterKeys`, `filterValues`, `filter`, `inverse` (list and set flavours), `plus`/`minus` for every overload, `orEmpty`, `isNotEmpty`, deprecated aliases still construct.

### Task 2: Table (subagent, worktree)

**Files:**
- Rewrite: `src/commonMain/kotlin/dev/ajthom/kollections/table/{Table,MutableTable,DelegatingTable,DelegatingMutableTable,SimpleTable,Tables}.kt`
- Create: `AbstractTable.kt`
- Tests: `src/commonTest/kotlin/dev/ajthom/kollections/table/{TableTest,MutableTableTest,TablesTest}.kt` (replace `SimpleTableTests.kt`, `TablesTests.kt`)

**Interfaces:** spec section "Table" verbatim. `DelegatingMutableTable` constructor:
`public open class DelegatingMutableTable<R, C, V>(backing: MutableMap<R, MutableMap<C, V>>, rowFactory: () -> MutableMap<C, V> = { LinkedHashMap() })`.
`row(rowKey)` on the mutable table returns a live `AbstractMutableMap<C, V>` view: `put` creates the backing row via `rowFactory` when absent, removing the last entry (via `remove` or the entries iterator) drops the row, `clear()` drops the row. `rowMap()` returns a read-only `AbstractMap<R, MutableMap<C, V>>` whose `get` returns the live row view for present rows and null otherwise, and whose `entries` is computed on each access.

Test cases: `get` hit/miss, `get` with absent row and present column elsewhere returns null, `size` counts cells, `put` returns previous value, `remove` returns value and drops empty row, `remove` on absent does not create a row, `putAll`, `clear`, `contains*`, `row` live view add/remove/clear, `rowMap()` live and read-only, `rowKeySet().remove` drops a row, `column`/`columnMap`/`columnKeySet`/`cellSet`/`values` contents and snapshot-ness, `transpose` content and independence from the source, equality across `SimpleTable`, `DelegatingTable`, and `tableOf`, `hashCode` equal for equal tables, `toString`, `forEach` order, `set` operator, `isNotEmpty`, `orEmpty`, `mapValues`, `filter`, `tableOf`, `mutableTableOf`, `toTable`/`toMutableTable` copy independence, `asTable` view reflects source, `buildTable`, deprecated `size()` and `asTable()` still work, custom `rowFactory` is used.

### Task 3: Multiset (subagent, worktree)

**Files:**
- Create: `src/commonMain/kotlin/dev/ajthom/kollections/multiset/{Multiset,MutableMultiset,LinkedHashMultiset,Multisets}.kt`
- Tests: `src/commonTest/kotlin/dev/ajthom/kollections/multiset/{LinkedHashMultisetTest,MultisetsTest}.kt`

**Interfaces:** spec section "Multiset" verbatim. `LinkedHashMultiset<E> : AbstractMutableCollection<E>(), MutableMultiset<E>`, `public constructor()` and `public constructor(elements: Iterable<E>)`.

Behaviour: `add(e, 0)` returns current count and changes nothing; negative occurrences throw `IllegalArgumentException`; `remove(e, n)` with n > count removes all; `setCount(e, 0)` removes; `setCount(e, old, new)` returns false and changes nothing when the current count is not `old`; `iterator().remove()` removes one occurrence; `elementSet.remove(e)` removes all occurrences; `contains`, `containsAll`, `isEmpty`, `size` total, `count` for absent = 0, `entries` in insertion order, equality independent of order, `hashCode` consistent, `toString` `[a x 2, b]`.

Factories: `emptyMultiset()`, `multisetOf(vararg)`, `mutableMultisetOf(vararg)`, `Iterable<E>.toMultiset()`, `Iterable<E>.toMutableMultiset()`, `Map<E, Int>.toMultiset()` (rejects negative counts, ignores zero counts).

### Task 4: BiMap (subagent, worktree)

**Files:**
- Create: `src/commonMain/kotlin/dev/ajthom/kollections/bimap/{BiMap,MutableBiMap,HashBiMap,BiMaps}.kt`
- Tests: `src/commonTest/kotlin/dev/ajthom/kollections/bimap/{HashBiMapTest,BiMapsTest}.kt`

**Interfaces:** spec section "BiMap" verbatim. `HashBiMap<K, V> : AbstractMutableMap<K, V>(), MutableBiMap<K, V>` with `public constructor()`; the inverse shares both backing maps and `inverse().inverse() === this`.

Behaviour: `put` new pair; `put` same key rebinds and frees the old value; `put` value bound to another key throws `IllegalArgumentException`; `put` same key same value is a no-op returning the value; `forcePut` evicts the other key; `remove(key)` updates inverse; `inverse()[v]` reflects; `keys.remove`, `values.remove`, `entries.iterator().remove()`, `entry.setValue` all keep both directions in sync and `setValue` to a value bound elsewhere throws; `clear`; `containsValue` is O(1) via the inverse; equality with a plain `Map` of the same content; `biMapOf` with duplicate values throws; `toBiMap`/`toMutableBiMap` copies; `emptyBiMap`.

### Task 5: Maps, Sets, Lists, Iterables (subagent, worktree)

**Files:**
- Rewrite: `src/commonMain/kotlin/dev/ajthom/kollections/map/Maps.kt`, `set/Sets.kt`, `list/Lists.kt`, `iterable/Iterables.kt`, `iterable/Iterators.kt`
- Create: `map/MapDifference.kt`, `set/ImmutableSet.kt`
- Tests: `src/commonTest/kotlin/dev/ajthom/kollections/{map/MapsTest,map/MapDifferenceTest,set/SetsTest,set/ImmutableSetTest,list/ImmutableListTest,map/ImmutableMapTest,iterable/IterablesTest,iterable/IteratorsTest}.kt`

**Interfaces:** spec section "Maps, Sets, Lists, Iterables" verbatim. Keep `public fun <K, V> Map<K, V>?.orEmpty(): Map<K, V>` in `Maps.kt` (used by other packages). Keep the `Iterables`, `Iterators`, `ImmutableList`, `ImmutableMap`, `Sets` objects with `@JvmStatic` members.

Behaviour to test: `Maps.difference` on equal maps (`areEqual`, all maps but `entriesInCommon` empty), only-left, only-right, differing values with `ValueDifference`, equality of two differences; `Sets.union/difference/symmetricDifference/intersection`; `powerSet` of a 3-element set has 8 members including the empty set and the full set, `contains`, and `powerSet` of 31 elements throws; `combinations(setOf(1,2,3), 2)` == `{{1,2},{1,3},{2,3}}`, k=0 gives `{{}}`, k>n gives empty, k<0 throws; `cartesianProduct(setOf(1,2), setOf("a","b"))` has 4 lists in odometer order, an empty input set gives an empty product, no sets gives one empty list; `ImmutableMap.of` 0..10 pairs and `copyOf`; `ImmutableSet.of` dedupes; `ImmutableList.copyOf`; `Iterables.only` on single-pass iterable with default (`Sequence.asIterable()`), `only` throws on 2+, `only` on empty throws `NoSuchElementException`, `getFirst` default with null first element returns null not default, `getLast` default; the same for `Iterators`.

### Task 6: Integration (main session)

- [ ] Copy each subagent's `src/` files into the main tree; remove worktrees.
- [ ] `./gradlew jvmTest` green with strict `explicitApi()`.
- [ ] Grep for tabs in `src/`: none.

### Task 7: Tooling and docs (main session)

**Files:** `build.gradle.kts`, `.github/workflows/{ci,publish,docs}.yml`, `.github/dependabot.yml`, `CHANGELOG.md`, `README.md`, `api/kollections.api`.

- [ ] Add `org.jetbrains.kotlinx.binary-compatibility-validator`; run `apiDump`; commit-ready `api/kollections.api`.
- [ ] Add ktlint Gradle plugin; run `ktlintFormat`; `ktlintCheck` passes.
- [ ] `ci.yml`: upload `build/reports/tests` and `build/test-results` with `if: failure()`.
- [ ] `publish.yml`: step that compares `${GITHUB_REF_NAME#v}` with `VERSION_NAME` from `gradle.properties` on release events and fails on mismatch.
- [ ] `docs.yml`: on push to master, `./gradlew dokkaGeneratePublicationHtml`, upload `build/dokka/html`, deploy with `actions/deploy-pages@v4`.
- [ ] `dependabot.yml`: `gradle` and `github-actions`, weekly.
- [ ] `CHANGELOG.md`: Keep a Changelog format, `2.0.0` with Added/Changed/Fixed/Removed and a migration section; `1.0.22` as the prior release.
- [ ] `README.md`: version 2.0.0, Maven Central badge, examples for every family, view semantics note, docs link, changelog link.

### Task 8: Verification (main session)

- [ ] `./gradlew build` on macOS: all targets compile, JVM/JS/wasmJs/native tests pass, `apiCheck` and `ktlintCheck` pass.
- [ ] Independent code review of the diff by a reviewer subagent; fix confirmed findings; re-run `./gradlew jvmTest apiCheck ktlintCheck`.
