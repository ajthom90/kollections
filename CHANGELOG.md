# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project
adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [2.0.0] - 2026-09-08

A ground-up revision of the public API. Every collection family now has read-only and mutable
interfaces, value equality, documented live-view semantics, and stdlib-style factories. See
"Migrating from 1.x" below.

### Added

- `SetMultimap` / `MutableSetMultimap` (values are sets) alongside `ListMultimap` /
  `MutableListMultimap`; `Multimap` is the common parent.
- `Multiset` / `MutableMultiset` with `LinkedHashMultiset`, `multisetOf`, `mutableMultisetOf`,
  `toMultiset`, `toMutableMultiset`, and `Map<E, Int>.toMultiset`.
- `BiMap` / `MutableBiMap` with `HashBiMap`, `biMapOf`, `mutableBiMapOf`, `toBiMap`,
  `toMutableBiMap`, `inverse()`, and `forcePut`.
- `Maps.difference` / `Map.difference` returning a `MapDifference`.
- `Sets.union`, `difference`, `symmetricDifference`, `powerSet`, `combinations`, and
  `cartesianProduct`.
- `ImmutableSet`; `ImmutableMap.of` for up to 10 pairs; `copyOf` on `ImmutableList`,
  `ImmutableSet`, and `ImmutableMap`.
- `Iterables.getLast(iterable, default)` and `Iterators.getLast(iterator, default)`.
- Multimap: `containsEntry`, `forEach`, `remove(key, value)`, `replaceValues`,
  `putAll(Multimap)`, `multimapOf(vararg pairs)`, `setMultimapOf`, `mutableSetMultimapOf`,
  `toMultimap` / `toSetMultimap` / `toMutableMultimap` / `toMutableSetMultimap` from pairs,
  maps, and multimaps, `asMultimap` / `asSetMultimap` views, `buildSetMultimap`,
  `multimapWith(keySelector, valueTransform)`, `mapKeys`, `filterKeys`, `filterValues`, `filter`,
  `inverse`, `plus` / `minus` operators, `plusAssign` / `minusAssign`, `iterator()` so
  `for ((key, value) in multimap)` works, `orEmpty`, and `isNotEmpty`.
- Table: `putAll`, `forEach`, `set` operator (`table[row, column] = value`), `tableOf`,
  `mutableTableOf`, `emptyTable`, `toTable` / `toMutableTable` from triples, tables, and nested
  maps, `asTable` view over a nested map, `mapValues`, `filter`, `orEmpty`, `isNotEmpty`, and a
  `rowFactory` constructor parameter on `DelegatingMutableTable` for custom row map types.
- `equals`, `hashCode`, and `toString` on every multimap, table, multiset, and bimap.
- Build: `explicitApi()`, binary-compatibility validation (`api/kollections.api`), ktlint, a
  `wasmJs` target, Dokka reference published to GitHub Pages, Dependabot, test-report upload in
  CI, and a release-tag check in the publish workflow.

### Changed

- **`Multimap.size` counts key-value pairs**, not keys, matching Guava. Use `keys.size` for
  the number of keys.
- **`Table.size` is a property that counts cells**, not a function that counts rows. Use
  `rowKeySet().size` for the number of rows. The old `size()` remains as a deprecated extension.
- `Multimap.get` returns `Collection<V>`; `ListMultimap.get` returns `List<V>` and
  `SetMultimap.get` returns `Set<V>`. All 1.x factories return `ListMultimap`, so code that
  indexed into `multimap[key]` keeps compiling.
- `Multimap.entries` is now `Set<Map.Entry<K, Collection<V>>>`; the `Multimap.Entry` interface
  is gone. Destructuring `for ((key, values) in multimap.entries)` still works.
- `MutableMultimap.get(key)` returns a **live view**: adding to it inserts into the multimap and
  removing its last element drops the key. `keys` and `asMap()` are live too; `values`,
  `flatValues`, and `entries` are snapshots.
- `Multimap.values` is `Collection<Collection<V>>` (was `List<List<V>>`) and `Multimap.flatValues` is
  `Collection<V>` (was `List<V>`). Index into them with `.toList()`, or iterate them.
- `MutableMultimap.asMap()` returns a read-only `Map<K, MutableCollection<V>>` (was
  `MutableMap<K, MutableList<V>>`), so `asMap().remove(key)` and `asMap()[key] = ...` no longer
  compile. Use `remove(key)` and `replaceValues(key, values)`; the value collections stay live.
- `MutableMultimap.values`, `entries`, and `flatValues` are read-only snapshots (they were
  `MutableList` / `MutableSet`), so they can no longer be mutated to change the multimap.
- `MutableMultimap.remove(key)` returns `Collection<V>` (was `MutableList<V>`); on a
  `MutableListMultimap` it returns `List<V>` and on a `MutableSetMultimap` a `Set<V>`.
- `MutableTable.row(rowKey)`, `rowKeySet()`, and `rowMap()` are live views; `column`,
  `columnMap`, `columnKeySet`, `cellSet`, and `values` are snapshots; `transpose()` is a copy.
- `MutableTable.cellSet()`, `columnKeySet()`, `columnMap()`, and `values()` are read-only (they
  were `Mutable*`), and `rowMap()` is `Map<R, MutableMap<C, V>>`: the outer map is read-only
  while each row stays a live `MutableMap`. Mutate through `put`, `remove`, or `row(rowKey)`.
- `multimapOf(map)` **copies** the map. Use `map.asMultimap()` for the old wrap-by-reference
  behaviour.
- `MutableMultimap.put` and `putAll` return `Boolean` (whether the multimap changed).
- `MutableMultimap.putAll(Map)` accepts `Map<out K, Iterable<V>>`.
- `multimapWith` is defined on `Iterable` instead of `Collection`, and the multimap parameter of
  `multimapWith(destination, keySelector)` is renamed from `multimap` to `destination`, so calls
  that passed it by name must use `multimapWith(destination = ...)`.
- `Iterables.only` / `Iterators.only` throw `IllegalArgumentException` (was
  `IllegalStateException`) when there is more than one element.
- Version, group, and artifact id are single-sourced from `gradle.properties`.
- Renamed implementation classes; the old names remain as deprecated type aliases:
  `DelegatingMultimap` -> `DelegatingListMultimap`,
  `DelegatingMutableMultimap` -> `DelegatingMutableListMultimap`,
  `MutableLinkedHashMultimap` -> `LinkedHashListMultimap`.
- `MutableTable.asTable()` moved from a member of the `MutableTable` interface to a deprecated
  extension function, in favour of `toTable()`; implementors of `MutableTable` outside this
  library must drop their `asTable()` override.

### Deprecated

- `dev.ajthom.kollections.map.orEmpty` for `Map`, which duplicates the standard library's
  `kotlin.collections.orEmpty`. Drop the import and use the standard library one; this copy will be
  removed in 3.0.

### Fixed

- `MutableMultimap.get(key)` on an absent key returned a detached list, so `multimap[key].add(x)`
  silently did nothing.
- `Table.get` scanned every row through `containsColumn` before each lookup.
- `Iterables.only(iterable, default)` created two iterators and failed on single-pass iterables.
- `Iterables.getFirst(iterable, default)` returned the default when the first element was `null`.
- Tests wrapped several checks in one `assertTrue { }` block and only asserted the last one.

### Removed

- `dev.ajthom.kollections.set.Multiset` (an unimplemented interface); replaced by
  `dev.ajthom.kollections.multiset.Multiset`.
- `Multimap.Entry` and `MutableMultimap.MutableEntry`.
- Unused internal helpers in `Maps`.

### Migrating from 1.x

| 1.x | 2.0 |
|---|---|
| `multimap.size` (keys) | `multimap.keys.size` |
| `table.size()` (rows) | `table.rowKeySet().size`; `table.size` is the cell count |
| `multimapOf(map)` as a view | `map.asMultimap()` |
| `mutableMultimap.remove(key)` | unchanged; `remove(key, value)` removes one pair |
| `mutableTable.asTable()` | `mutableTable.toTable()` |
| `MutableLinkedHashMultimap()` | `LinkedHashListMultimap()` or `mutableMultimapOf()` |
| `DelegatingMultimap(map)` | `DelegatingListMultimap(map)` or `map.asMultimap()` |
| `Multimap.Entry` | `Map.Entry<K, Collection<V>>` |
| `multimap.values[0]` / `multimap.flatValues[0]` | `multimap.values.toList()[0]`, or iterate |
| `mutableMultimap.asMap().remove(key)` | `mutableMultimap.remove(key)` |
| `mutableMultimap.asMap()[key] = values` | `mutableMultimap.replaceValues(key, values)` |
| `mutableMultimap.entries` / `values` as mutable | read-only snapshots; mutate the multimap |
| `mutableTable.cellSet().remove(cell)` | `mutableTable.remove(cell.rowKey, cell.columnKey)` |
| `multimapWith(multimap = dest) { ... }` | `multimapWith(destination = dest) { ... }` |

## [1.0.22] - 2026-09-08

- Last release of the 1.x line. Published to Maven Central under `io.github.ajthom90:kollections`.

[Unreleased]: https://github.com/ajthom90/kollections/compare/v2.0.0...HEAD
[2.0.0]: https://github.com/ajthom90/kollections/compare/v1.0.22...v2.0.0
[1.0.22]: https://github.com/ajthom90/kollections/releases/tag/v1.0.22
