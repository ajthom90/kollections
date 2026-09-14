package dev.ajthom.kollections.multimap

/**
 * A [MutableListMultimap] that keeps keys in insertion order and the values of each key in insertion order.
 *
 * @param K the type of the keys
 * @param V the type of the values
 */
public class LinkedHashListMultimap<K, V> : DelegatingMutableListMultimap<K, V>(LinkedHashMap())
