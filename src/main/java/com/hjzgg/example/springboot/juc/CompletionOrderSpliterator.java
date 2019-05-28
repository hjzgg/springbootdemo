package com.hjzgg.example.springboot.juc;

import javafx.util.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

final class CompletionOrderSpliterator<T> implements Spliterator<T> {
    private final Map<Integer, CompletableFuture<Pair<Integer, T>>> indexedFutures;

    CompletionOrderSpliterator(Collection<CompletableFuture<T>> futures) {
        indexedFutures = toIndexedFutures(futures);
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (!indexedFutures.isEmpty()) {
            action.accept(nextCompleted());
            return true;
        } else {
            return false;
        }
    }

    private T nextCompleted() {
        return CompletableFuture.anyOf(indexedFutures.values().toArray(new CompletableFuture[0]))
                .thenApply(result -> ((Pair<Integer, T>) result))
                .thenApply(result -> {
                    indexedFutures.remove(result.getKey());
                    return result.getValue();
                }).join();
    }

    @Override
    public Spliterator<T> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return indexedFutures.size();
    }

    @Override
    public int characteristics() {
        return SIZED & IMMUTABLE & NONNULL;
    }

    private static <T> Map<Integer, CompletableFuture<Pair<Integer, T>>> toIndexedFutures(Collection<CompletableFuture<T>> futures) {
        Map<Integer, CompletableFuture<Pair<Integer, T>>> map = new HashMap<>(futures.size(), 1);
        int counter = 0;
        for (CompletableFuture<T> f : futures) {
            int index = counter++;
            map.put(index, f.thenApply(value -> new Pair<>(index, value)));
        }
        return map;
    }
}