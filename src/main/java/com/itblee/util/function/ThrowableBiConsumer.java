package com.itblee.util.function;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface ThrowableBiConsumer<K, V> extends BiConsumer<K, V> {

    @Override
    default void accept(final K k, final V v) {
        try {
            acceptThrows(k, v);
        } catch (final Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    void acceptThrows(K k, V v) throws Exception;

}