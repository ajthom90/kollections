package dev.ajthom.kollections.multimap

class MutableLinkedHashMultimap<KeyType, ValueType>: DelegatingMutableMultimap<KeyType, ValueType>(linkedMapOf())
