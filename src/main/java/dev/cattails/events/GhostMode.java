package dev.cattails.events;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EcsEvent;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.event.events.ecs.InteractivelyPickupItemEvent;
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent;
import com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.cattails.util.PlayerStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static dev.cattails.SurviveTheNight.INSTANCE;

public final class GhostMode {

    private GhostMode() {}

    public static boolean isGhost(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        PlayerStore ps = store.getComponent(ref, INSTANCE.getPlayerStoreComponent());
        return ps != null && Boolean.TRUE.equals(ps.getGhostMode());
    }

    private static abstract class GhostEventSystem<T extends EcsEvent> extends EntityEventSystem<EntityStore, T> {
        protected GhostEventSystem(@Nonnull Class<T> eventType) {
            super(eventType);
        }

        @Nullable
        @Override
        public Query<EntityStore> getQuery() {
            return Query.any();
        }
    }

    public static final class CancelBreakBlock extends GhostEventSystem<BreakBlockEvent> {
        public CancelBreakBlock() { super(BreakBlockEvent.class); }

        @Override
        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> chunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> buffer, @Nonnull BreakBlockEvent event) {
            Ref<EntityStore> ref = chunk.getReferenceTo(index);
            if (isGhost(ref, store)) event.setCancelled(true);
        }
    }

    public static final class CancelPlaceBlock extends GhostEventSystem<PlaceBlockEvent> {
        public CancelPlaceBlock() { super(PlaceBlockEvent.class); }

        @Override
        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> chunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> buffer, @Nonnull PlaceBlockEvent event) {
            Ref<EntityStore> ref = chunk.getReferenceTo(index);
            if (isGhost(ref, store)) event.setCancelled(true);
        }
    }

    public static final class CancelUseBlockPre extends GhostEventSystem<UseBlockEvent.Pre> {
        public CancelUseBlockPre() { super(UseBlockEvent.Pre.class); }

        @Override
        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> chunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> buffer, @Nonnull UseBlockEvent.Pre event) {
            Ref<EntityStore> ref = chunk.getReferenceTo(index);
            if (isGhost(ref, store)) event.setCancelled(true);
        }
    }

    public static final class CancelPickupItem extends GhostEventSystem<InteractivelyPickupItemEvent> {
        public CancelPickupItem() { super(InteractivelyPickupItemEvent.class); }

        @Override
        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> chunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> buffer, @Nonnull InteractivelyPickupItemEvent event) {
            Ref<EntityStore> ref = chunk.getReferenceTo(index);
            if (isGhost(ref, store)) event.setCancelled(true);
        }
    }
}
