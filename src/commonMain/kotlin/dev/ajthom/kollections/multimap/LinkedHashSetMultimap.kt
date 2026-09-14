package dev.ajthom.kollections.multimap

/**
 * A [MutableSetMultimap] that keeps keys in insertion order and the distinct values of each key in insertion
 * order.
 *
 * @param K the type of the keys
 * @param V the type of the values
 */
public class LinkedHashSetMultimap<K, V> : DelegatingMutableSetMultimap<K, V>(LinkedHashMap())
