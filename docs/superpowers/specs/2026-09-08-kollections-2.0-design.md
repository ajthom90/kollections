# kollections 2.0 design

Date: 2026-09-08. Status: approved by the maintainer ("implement all your suggestions")
on the basis of the assessment that produced this list. Detailed decisions below were
made by Claude and are recorded so they can be reviewed.

## Goals

1. Fix correctness problems in 1.0.x: non-live "mutable" views, missing equality, O(rows)
   table lookups, single-pass iterable bugs, tests that assert nothing.
2. Round out the Guava-style surface: SetMultimap, Multiset, BiMap, MapDifference,
   Sets helpers, idiomatic factories and operators.
3. Harden the build for a public library: explicit API, binary-compatibility validation,
   single-sourced version, more targets, docs site, changelog, lint.

Version becomes **2.0.0**. Breaking changes are listed in CHANGELOG.md.

## Conventions

- Package root `dev.ajthom.kollections`. Subpackages: `multimap`, `table`, `multiset`
  (new, replaces `set.Multiset`), `bimap` (new), `map`, `set`, `list`, `iterable`.
- `explicitApi()` is on: every public declaration has an explicit `public` modifier and
  explicit return type.
- Every public declaration has KDoc.
- 4-space indentation (official Kotlin style), enforced by `.editorconfig` and ktlint.
- Read-only interfaces have no `Mutable` prefix; mutable ones do, matching the stdlib.
- Factories follow stdlib naming: `xOf(vararg)`, `mutableXOf(vararg)`, `emptyX()`,
  `toX()` copies, `asX()` wraps by reference, `buildX {}` uses a builder lambda.
- Equality: two multimaps, tables, multisets, or bimaps are equal when they hold the same
  content. `hashCode` and `toString` are consistent with equality.
- Live views vs. snapshots are stated in KDoc for every accessor of a mutable type.

## Multimap

```kotlin
interface Multimap<K, V> {
    val size: Int                      // number of key-value pairs (was: number of keys)
    val keys: Set<K>
    val values: Collection<Collection<V>>
    val flatValues: Collection<V>
    val entries: Set<Map.Entry<K, Collection<V>>>   // Multimap.Entry is removed
    operator fun get(key: K): Collection<V>
    fun asMap(): Map<K, Collection<V>>
    fun isEmpty(): Boolean
    fun containsKey(key: K): Boolean
    fun containsValue(value: V): Boolean
    fun containsEntry(key: K, value: V): Boolean
    fun forEach(action: (key: K, value: V) -> Unit)   // default implementation
}
interface ListMultimap<K, V> : Multimap<K, V>   // get/values/entries/asMap narrowed to List
interface SetMultimap<K, V>  : Multimap<K, V>   // narrowed to Set

interface MutableMultimap<K, V> : Multimap<K, V> {
    override operator fun get(key: K): MutableCollection<V>   // LIVE view; adding to it inserts
    override val keys: MutableSet<K>                          // LIVE; removing a key drops its values
    override fun asMap(): Map<K, MutableCollection<V>>        // LIVE read-only map; values are live views
    // values, flatValues, entries are snapshots
    fun put(key: K, value: V): Boolean
    fun putAll(key: K, values: Iterable<V>): Boolean
    fun putAll(from: Multimap<out K, out V>): Boolean
    fun putAll(from: Map<out K, Iterable<V>>): Boolean
    fun remove(key: K, value: V): Boolean
    fun remove(key: K): Collection<V>                          // removes all values, returns them
    fun replaceValues(key: K, values: Iterable<V>): Collection<V>
    fun clear()
}
interface MutableListMultimap<K, V> : MutableMultimap<K, V>, ListMultimap<K, V>  // get(): MutableList
interface MutableSetMultimap<K, V>  : MutableMultimap<K, V>, SetMultimap<K, V>   // get(): MutableSet
```

Invariant: no key maps to an empty collection. Live views remove the key when they empty
and re-create the backing collection when something is added to a view of an absent key.

Classes:

- `AbstractMultimap<K, V, C : Collection<V>>` (public, abstract): implements everything from
  `asMap(): Map<K, C>`, plus `equals`/`hashCode`/`toString` delegating to `asMap()`.
  A `ListMultimap` never equals a `SetMultimap` (List != Set), matching Guava.
- `DelegatingListMultimap(map: Map<K, List<V>>)`, `DelegatingSetMultimap(map: Map<K, Set<V>>)`:
  read-only wrappers by reference.
- `AbstractMutableMultimap<K, V, C : MutableCollection<V>>(backing: MutableMap<K, C>)`.
- `DelegatingMutableListMultimap(backing, listFactory = { ArrayList() })`,
  `DelegatingMutableSetMultimap(backing, setFactory = { LinkedHashSet() })`: the factory
  parameters let callers choose the value collection type (JVM users can pass TreeMap/TreeSet
  for sorted behaviour, Guava's `newCustomTable` idea).
- `LinkedHashListMultimap`, `LinkedHashSetMultimap`: insertion-ordered concrete classes.
- Deprecated typealiases: `DelegatingMultimap` -> `DelegatingListMultimap`,
  `DelegatingMutableMultimap` -> `DelegatingMutableListMultimap`,
  `MutableLinkedHashMultimap` -> `LinkedHashListMultimap`.

Factories and extensions (`Multimaps.kt`); the unqualified name is the List flavour:

- `emptyMultimap()`, `emptySetMultimap()`
- `multimapOf(vararg Pair)`, `multimapOf(Map<K, Iterable<V>>)` (copies; was a view),
  `setMultimapOf(...)`, `mutableMultimapOf(vararg)`, `mutableSetMultimapOf(vararg)`
- `Iterable<Pair<K, V>>.toMultimap()/.toSetMultimap()/.toMutableMultimap()/.toMutableSetMultimap()`
- `Map<K, Iterable<V>>.toMultimap()/... (same four)`
- `Multimap<K, V>.toMultimap()/... (same four)`
- `Map<K, List<V>>.asMultimap()`, `Map<K, Set<V>>.asSetMultimap()` (views by reference)
- `buildMultimap {}`, `buildSetMultimap {}`
- `Iterable<V>.multimapWith(keySelector)`, `multimapWith(keySelector, valueTransform)`,
  `multimapWith(destination, keySelector)`
- `transformValues`, `mapKeys`, `filterKeys`, `filterValues`, `filter((K, V) -> Boolean)`,
  `inverse()` -- defined on `ListMultimap` (returns List flavour), `SetMultimap` (Set flavour),
  and `Multimap` (List flavour).
- Operators: `plus(Pair)`, `plus(Iterable<Pair>)`, `plus(Multimap)`, `minus(key)`,
  `minus(Iterable<K>)`, `minus(Pair)` on both flavours; `plusAssign`/`minusAssign` on
  `MutableMultimap`.
- `operator fun Multimap<K, V>.iterator(): Iterator<Pair<K, V>>` so `for ((k, v) in mm)` works.
- `Multimap<K, V>?.orEmpty()`, `Multimap<*, *>.isNotEmpty()`.

## Table

```kotlin
interface Table<R, C, V> {
    val size: Int                      // number of cells (was: size() = number of rows)
    fun isEmpty(): Boolean
    operator fun get(rowKey: R, columnKey: C): V?
    fun contains(rowKey: R, columnKey: C): Boolean
    fun containsRow(rowKey: R): Boolean
    fun containsColumn(columnKey: C): Boolean
    fun containsValue(value: V): Boolean
    fun row(rowKey: R): Map<C, V>
    fun column(columnKey: C): Map<R, V>
    fun rowKeySet(): Set<R>
    fun columnKeySet(): Set<C>
    fun rowMap(): Map<R, Map<C, V>>
    fun columnMap(): Map<C, Map<R, V>>
    fun cellSet(): Set<TableCell<R, C, V>>
    fun values(): Collection<V>
    fun transpose(): Table<C, R, V>
    fun forEach(action: (rowKey: R, columnKey: C, value: V) -> Unit)   // default
}
interface MutableTable<R, C, V> : Table<R, C, V> {
    override fun row(rowKey: R): MutableMap<C, V>        // LIVE view
    override fun rowKeySet(): MutableSet<R>              // LIVE
    override fun rowMap(): Map<R, MutableMap<C, V>>      // LIVE read-only map of live rows
    // column, columnMap, columnKeySet, cellSet, values are snapshots
    override fun transpose(): MutableTable<C, R, V>      // copy
    fun put(rowKey: R, columnKey: C, value: V): V?
    fun putAll(from: Table<out R, out C, out V>)
    fun remove(rowKey: R, columnKey: C): V?
    fun clear()
}
data class TableCell<R, C, V>(val rowKey: R, val columnKey: C, val value: V)
```

Invariant: no row maps to an empty map.

Classes: `AbstractTable` (equality on `cellSet()`, `toString` = `rowMap()`), `DelegatingTable(map)`
read-only view, `DelegatingMutableTable(backing, rowFactory = { LinkedHashMap() })`,
`SimpleTable()` = insertion ordered. `get` is a nested map lookup, `containsValue` streams.

Factories and extensions (`Tables.kt`): `emptyTable()`, `tableOf(vararg Triple)`,
`mutableTableOf(vararg Triple)`, `Iterable<Triple>.toTable()/.toMutableTable()`,
`Table.toTable()/.toMutableTable()`, `Map<R, Map<C, V>>.toTable()/.asTable()`,
`buildTable {}`, `operator fun MutableTable.set(r, c, v)`, `isNotEmpty()`, `orEmpty()`,
`mapValues((V) -> R)`, `filter((TableCell) -> Boolean)`.
Deprecated: `Table.size()` extension (use `size`), `MutableTable.asTable()` extension
(use `toTable()`).

A sorted (TreeBasedTable-style) implementation is not provided because common Kotlin has no
sorted map; JVM callers get it with `DelegatingMutableTable(TreeMap()) { TreeMap() }`.

## Multiset

```kotlin
interface Multiset<E> : Collection<E> {
    override val size: Int                  // total occurrences
    fun count(element: E): Int
    val elementSet: Set<E>
    val entries: Set<Multiset.Entry<E>>
    data class Entry<out E>(val element: E, val count: Int)
}
interface MutableMultiset<E> : Multiset<E>, MutableCollection<E> {
    override val elementSet: MutableSet<E>            // LIVE; removing drops all occurrences
    fun add(element: E, occurrences: Int): Int        // returns previous count
    fun remove(element: E, occurrences: Int): Int     // returns previous count
    fun setCount(element: E, count: Int): Int         // returns previous count
    fun setCount(element: E, oldCount: Int, newCount: Int): Boolean
}
```

`LinkedHashMultiset<E>` backed by `LinkedHashMap<E, Int>`; iterator yields each element
`count` times and supports `remove()` (one occurrence). Equality by element counts;
`hashCode` = sum of `element.hashCode() xor count`; `toString` like `[a x 2, b]`.
Factories: `emptyMultiset()`, `multisetOf(vararg)`, `mutableMultisetOf(vararg)`,
`Iterable<E>.toMultiset()/.toMutableMultiset()`, `Map<E, Int>.toMultiset()`.

## BiMap

```kotlin
interface BiMap<K, V> : Map<K, V> { override val values: Set<V>; fun inverse(): BiMap<V, K> }
interface MutableBiMap<K, V> : BiMap<K, V>, MutableMap<K, V> {
    override val values: MutableSet<V>
    override fun inverse(): MutableBiMap<V, K>
    fun forcePut(key: K, value: V): V?      // evicts any existing key bound to value
}
```

`put` throws `IllegalArgumentException` when the value is already bound to a different key.
`HashBiMap<K, V>` extends `AbstractMutableMap`, keeps two `LinkedHashMap`s, and `inverse()`
returns a view sharing both maps. `entries`, `keys`, `values` are live and keep both maps in
sync on removal and `setValue`. Factories: `emptyBiMap()`, `biMapOf(vararg)`,
`mutableBiMapOf(vararg)`, `Map<K, V>.toBiMap()/.toMutableBiMap()`.

## Maps, Sets, Lists, Iterables

- `Maps.difference(left, right): MapDifference<K, V>` and `Map<K, V>.difference(other)`.
  `MapDifference` exposes `areEqual`, `entriesOnlyOnLeft`, `entriesOnlyOnRight`,
  `entriesInCommon`, `entriesDiffering: Map<K, ValueDifference<V>>` where
  `data class ValueDifference<out V>(val leftValue: V, val rightValue: V)`.
  Equality on the four maps. The unused `safe*` helpers are deleted.
- `ImmutableMap.of` for 0..10 pairs, `ImmutableMap.copyOf(map)`;
  `ImmutableSet.of()/of(vararg)/of(item)/copyOf(iterable)`; `ImmutableList.copyOf(iterable)`.
  KDoc notes these return the stdlib's read-only collections.
- `Sets.intersection`, `union`, `difference`, `symmetricDifference`, `powerSet` (lazy set view,
  size 2^n, n <= 30), `combinations(set, k)` (lazy, size n choose k, must fit in Int),
  `cartesianProduct(vararg sets)` / `cartesianProduct(List<Set<T>>)` (lazy `Set<List<T>>`).
- `Iterables.only(iter, default)` uses a single iterator; `getFirst(iter, default)` handles a
  null first element; `getLast(iter, default)` added for `Iterables` and `Iterators`.
  `Iterators.getFirst(iter, default)` uses `hasNext()` rather than catching exceptions.

## Tooling

- `gradle.properties`: `GROUP`, `VERSION_NAME=2.0.0`, `POM_ARTIFACT_ID`; build script reads them.
- `explicitApi()`; `org.jetbrains.kotlinx.binary-compatibility-validator` with committed
  `api/kollections.api`; ktlint Gradle plugin wired into `check`.
- Target added: `wasmJs { nodejs() }`. `macosX64` was planned but is no longer an available
  Kotlin/Native target in Kotlin 2.3.x (the compiler reports it as removed), so it is not added.
  A JS `browser()` target is not added: the published klib is environment-agnostic, so it would
  only change where tests run.
- CI: unchanged matrix (macOS), uploads test reports on failure. New `docs.yml` publishes Dokka
  HTML to GitHub Pages (Pages must be set to "GitHub Actions" source once in repo settings).
  `publish.yml` fails if the release tag does not match `VERSION_NAME`.
- `.github/dependabot.yml` for gradle and github-actions. `.editorconfig`. `.DS_Store` ignored.
- `CHANGELOG.md` with the 2.0.0 entry and migration notes. README updated.

## Testing

kotlin.test in `commonTest`, run on every target. Each public function has at least one test.
Assertions are one per `assertX` call; no multi-statement `assertTrue { }` blocks. View
liveness, the empty-collection invariant, equality, and the documented exceptions are tested
explicitly.
