package com.luxof.lapisworks;

import java.util.Objects;

/** how to make the most cursed "system" ever */
public interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v);
    /** i do as the BiConsumer has. Basically merges this and the other TriConsumer. */
    default TriConsumer<T, U, V> andThen(TriConsumer<? super T, ? super U, ? super V> after) {
        Objects.requireNonNull(after);

        return (l, r, x) -> {
            accept(l, r, x);
            after.accept(l, r, x);
        };
    }
}
