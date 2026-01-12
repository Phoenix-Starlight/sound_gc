package org.fury_phoenix.soundgc.mixin;

import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;
import java.util.concurrent.*;

@Mixin(SoundBufferLibrary.class) public abstract class SoundBufferLibraryMixin {
    @Shadow
    @Final
    @Mutable
    private final Map<ResourceLocation, CompletableFuture<SoundBuffer>> cache = new Cache();
}


final class Cache implements Map<ResourceLocation, CompletableFuture<SoundBuffer>> {
    private final Map<ResourceLocation, Timestamp<CompletableFuture<SoundBuffer>>> map = new ConcurrentHashMap<>();

    private final Executor offThreadCleaner = Executors.newSingleThreadExecutor();

    private final static long SOUND_RETENTION_DURATION = 30;

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
        return map.values().stream().map(Timestamp::value).anyMatch(value::equals);
    }

    @Override public CompletableFuture<SoundBuffer> get(Object key) {
        return map.get(key).value();
    }

    @Override public CompletableFuture<SoundBuffer> put(ResourceLocation key, CompletableFuture<SoundBuffer> value) {
        var v = value.thenCompose(sound -> {
            map.put(
                key,
                new Timestamp<>(
                    System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(SOUND_RETENTION_DURATION),
                    value
                )
            );
            return value;
        });

        map.put(
            key,
            new Timestamp<>(
                Long.MAX_VALUE,
                v
            )
        );

        offThreadCleaner.execute(() -> map.forEach(this::expire));

        return v;
    }

    private void expire(ResourceLocation k, Timestamp<CompletableFuture<SoundBuffer>> timestamp) {
        if (System.currentTimeMillis() >= timestamp.timestamp()) {
            timestamp.value().thenAccept(SoundBuffer::discardAlBuffer);
            map.remove(k);
        }
    }

    @Override public CompletableFuture<SoundBuffer> remove(Object key) {
        return map.remove(key).value();
    }

    @Override public void putAll(@NotNull Map<? extends ResourceLocation, ? extends CompletableFuture<SoundBuffer>> m) {
        for (var e: m.entrySet()) {
            this.put(e.getKey(), e.getValue());
        }
    }

    @Override public void clear() {
        map.values().stream().map(Timestamp::value).forEach(f -> f.thenAccept(SoundBuffer::discardAlBuffer));
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
