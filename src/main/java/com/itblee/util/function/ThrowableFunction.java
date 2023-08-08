package com.itblee.util.function;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowableFunction<T, R> extends Function<T, R> {

    @Override
    default R apply(final T t) {
        try {
            return applyThrows(t);
        } catch (final Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    R applyThrows(T t) throws Exception;

}