package dev.ajthom.kollections.multimap

/** The 1.x name of [DelegatingListMultimap]. */
@Deprecated(
    message = "Renamed to DelegatingListMultimap.",
    replaceWith = ReplaceWith("DelegatingListMultimap", "dev.ajthom.kollections.multimap.DelegatingListMultimap"),
)
public typealias DelegatingMultimap<K, V> = DelegatingListMultimap<K, V>

/** The 1.x name of [DelegatingMutableListMultimap]. */
@Deprecated(
    message = "Renamed to DelegatingMutableListMultimap.",
    replaceWith = ReplaceWith(
        "DelegatingMutableListMultimap",
        "dev.ajthom.kollections.multimap.DelegatingMutableListMultimap",
    ),
)
public typealias DelegatingMutableMultimap<K, V> = DelegatingMutableListMultimap<K, V>

/** The 1.x name of [LinkedHashListMultimap]. */
@Deprecated(
    message = "Renamed to LinkedHashListMultimap.",
    replaceWith = ReplaceWith("LinkedHashListMultimap", "dev.ajthom.kollections.multimap.LinkedHashListMultimap"),
)
public typealias MutableLinkedHashMultimap<K, V> = LinkedHashListMultimap<K, V>
