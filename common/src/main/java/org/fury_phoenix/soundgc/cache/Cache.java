package org.fury_phoenix.soundgc.cache;

import com.github.bsideup.jabel.Desugar;
import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fury_phoenix.soundgc.SoundGC;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.*;

public final class Cache implements Map<ResourceLocation, CompletableFuture<SoundBuffer>> {
    private final Map<ResourceLocation, Cache.Timestamp<CompletableFuture<SoundBuffer>>> map = new ConcurrentHashMap<>();

    private final Executor offThreadCleaner = Executors.newSingleThreadExecutor();

    private final static long SOUND_RETENTION_DURATION = 30;

    private final static boolean debug = Boolean.getBoolean("soundgc.debug");

    @Desugar
    private record Timestamp<V>(long timestamp, V value) {
    }

    @Override public int size() {
        return map.size();
    }

    @Override public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override public boolean containsValue(Object value) {
        return map.values().stream().map(Cache.Timestamp::value).anyMatch(value::equals);
    }

    @Override public CompletableFuture<SoundBuffer> get(Object key) {
        Timestamp<CompletableFuture<SoundBuffer>> timestamp = map.get(key);
        return timestamp == null ? null : timestamp.value();
    }

    @Override public CompletableFuture<SoundBuffer> put(ResourceLocation key, CompletableFuture<SoundBuffer> value) {
        var v = value.thenCompose(sound -> {
            Cache.Timestamp<CompletableFuture<SoundBuffer>> timestamp = new Cache.Timestamp<>(
                System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(SOUND_RETENTION_DURATION),
                value
            );

            if (debug) SoundGC.LOGGER.debug(timestamp);

            map.put(
                key,
                timestamp
            );
            return value;
        });

        map.put(
            key,
            new Cache.Timestamp<>(
                Long.MAX_VALUE,
                v
            )
        );

//        offThreadCleaner.execute(() -> map.forEach(this::expire));

        return v;
    }

    private void expire(ResourceLocation k, Cache.Timestamp<CompletableFuture<SoundBuffer>> timestamp) {
        if (System.currentTimeMillis() >= timestamp.timestamp()) {
            timestamp.value().thenAccept(SoundBuffer::discardAlBuffer);
            map.remove(k);
        }
    }

    @Override public CompletableFuture<SoundBuffer> remove(Object key) {
        return map.remove(key).value();
    }

    @Override public void putAll(@NotNull Map<? extends ResourceLocation, ? extends CompletableFuture<SoundBuffer>> m) {
        for (var e : m.entrySet()) {
            this.put(
                e.getKey(),
                e.getValue()
            );
        }
    }

    @Override public void clear() {
        map.values().stream().map(Cache.Timestamp::value).forEach(f -> f.thenAccept(SoundBuffer::discardAlBuffer));
    }

    @Override public @NotNull Set<ResourceLocation> keySet() {
        return map.keySet();
    }

    @Override public @NotNull Collection<CompletableFuture<SoundBuffer>> values() {
        return new AbstractCollection<>() {
            @Override public @NotNull Iterator<CompletableFuture<SoundBuffer>> iterator() {
                var it = map.values().iterator();
                return new Iterator<>() {
                    @Override public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override public CompletableFuture<SoundBuffer> next() {
                        return it.next().value();
                    }
                };
            }

            @Override public int size() {
                return map.size();
            }
        };
    }

    @Override public @NotNull Set<Entry<ResourceLocation, CompletableFuture<SoundBuffer>>> entrySet() {
        return new AbstractSet<>() {
            @Override public @NotNull Iterator<Entry<ResourceLocation, CompletableFuture<SoundBuffer>>> iterator() {
                var it = map.entrySet().iterator();
                return new Iterator<>() {
                    @Override public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override public Entry<ResourceLocation, CompletableFuture<SoundBuffer>> next() {
                        var item = it.next();
                        return new AbstractMap.SimpleImmutableEntry<>(
                            item.getKey(),
                            item.getValue().value()
                        );
                    }
                };
            }

            @Override public int size() {
                return map.size();
            }
        };
    }
}
